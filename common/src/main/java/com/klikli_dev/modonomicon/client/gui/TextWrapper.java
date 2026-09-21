/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.client.gui;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.StringDecomposer;

/**
 * Wraps formatted text into lines using {@link BreakIterator} line break opportunities.
 * <p>
 * Vanilla's {@link net.minecraft.client.StringSplitter} only breaks at spaces, falling back to breaking unbreakable
 * runs per codepoint. For mixed latin/CJK text this cuts lines short: once a line overflows it breaks at the last
 * space, discarding all CJK characters that would still have fit after it. Using the text's line break rules instead
 * allows breaking between CJK characters, so such lines are filled up to the available width.
 */
public final class TextWrapper {

	private static final int CACHE_CAPACITY = 512;

	/**
	 * Rendering happens on the client thread, so a plain LRU is sufficient. It is cleared on resource reload because
	 * glyph advances (and thus line breaking) can change when fonts change.
	 */
	private static final Map<CacheKey, List<Line>> CACHE = new LinkedHashMap<>(64, 0.75f, true) {
		@Override
		protected boolean removeEldestEntry(Map.Entry<CacheKey, List<Line>> eldest) {
			return this.size() > CACHE_CAPACITY;
		}
	};

	private TextWrapper() {
	}

	public static void clearCache() {
		CACHE.clear();
	}

	/**
	 * Drop-in replacement for {@link Font#split(FormattedText, int)}.
	 */
	public static List<FormattedCharSequence> split(FormattedText text, int maxWidth, Font font) {
		List<Line> lines = splitLines(text, maxWidth, Style.EMPTY, font);
		List<FormattedCharSequence> result = new ArrayList<>(lines.size());
		for (Line line : lines) {
			result.add(Language.getInstance().getVisualOrder(line.text()));
		}
		return result;
	}

	/**
	 * Wraps the given text and reports each line. The wrapped flag matches vanilla
	 * {@link net.minecraft.client.StringSplitter#splitLines(FormattedText, int, Style, BiConsumer)}: it is {@code true}
	 * for lines created by a soft wrap, and {@code false} for the first line of the text or a line after a hard line
	 * break.
	 */
	public static void splitLines(
			FormattedText text,
			int maxWidth,
			Style initialStyle,
			Font font,
			BiConsumer<FormattedText, Boolean> output) {
		for (Line line : splitLines(text, maxWidth, initialStyle, font)) {
			output.accept(line.text(), line.isWrapped());
		}
	}

	private static List<Line> splitLines(FormattedText text, int maxWidth, Style initialStyle, Font font) {
		NormalizedText normalized = normalize(text, initialStyle);
		if (normalized.plain().isEmpty()) {
			return List.of();
		}

		CacheKey key = new CacheKey(normalized, Math.max(maxWidth, 1), initialStyle, currentLocale(), font);
		List<Line> cached = CACHE.get(key);
		if (cached != null) {
			return cached;
		}

		List<Line> lines = computeLines(normalized, key.maxWidth(), key.locale(), font);
		CACHE.put(key, lines);
		return lines;
	}

	private static List<Line> computeLines(NormalizedText normalized, int maxWidth, Locale locale, Font font) {
		List<Atom> atoms = new ArrayList<>();

		BreakIterator boundary = BreakIterator.getLineInstance(locale);
		boundary.setText(normalized.plain());

		int unitStart = boundary.first();
		for (int unitEnd = boundary.next(); unitEnd != BreakIterator.DONE; unitStart = unitEnd, unitEnd = boundary.next()) {
			//break opportunities attach trailing whitespace and newlines to the preceding unit
			boolean forcedBreak = normalized.plain().charAt(unitEnd - 1) == '\n';
			int contentEnd = forcedBreak ? unitEnd - 1 : unitEnd;

			if (contentEnd > unitStart && width(normalized, unitStart, contentEnd, font) > maxWidth) {
				//the unit does not even fit on a line of its own, so allow breaking inside it like vanilla does for
				//unbreakable runs
				int codepointStart = unitStart;
				while (codepointStart < contentEnd) {
					int codepointEnd = codepointStart + Character.charCount(normalized.plain().codePointAt(codepointStart));
					boolean isLast = codepointEnd >= contentEnd;
					atoms.add(new Atom(
							codepointStart,
							codepointEnd,
							width(normalized, codepointStart, codepointEnd, font),
							isLast && forcedBreak,
							isLast ? unitEnd : codepointEnd));
					codepointStart = codepointEnd;
				}
			} else {
				atoms.add(new Atom(unitStart, contentEnd, width(normalized, unitStart, contentEnd, font), forcedBreak, unitEnd));
			}
		}

		List<Line> lines = new ArrayList<>();
		int lineStart = 0;
		int filled = 0;
		float lineWidth = 0.0f;
		boolean lineHasContent = false;
		boolean isWrapped = false;
		boolean lastWasForcedBreak = false;

		for (Atom atom : atoms) {
			if (lineHasContent && lineWidth + atom.width() > maxWidth) {
				lines.add(new Line(slice(normalized, lineStart, filled), isWrapped));
				isWrapped = true;
				lineStart = filled;
				lineWidth = 0.0f;
				lineHasContent = false;
			}

			if (atom.end() > atom.start()) {
				lineWidth += atom.width();
				filled = atom.end();
				lineHasContent = true;
			} else {
				filled = atom.start();
			}

			if (atom.forcedBreak()) {
				lines.add(new Line(slice(normalized, lineStart, filled), isWrapped));
				isWrapped = false;
				lastWasForcedBreak = true;
				lineStart = atom.next();
				filled = atom.next();
				lineWidth = 0.0f;
				lineHasContent = false;
			}
		}

		if (lineHasContent) {
			lines.add(new Line(slice(normalized, lineStart, filled), isWrapped));
		} else if (lastWasForcedBreak) {
			lines.add(new Line(FormattedText.EMPTY, false));
		}

		return lines;
	}

	private static float width(NormalizedText normalized, int start, int end, Font font) {
		if (start >= end) {
			return 0.0f;
		}

		float result = 0.0f;
		int cursor = 0;
		for (Segment segment : normalized.segments()) {
			int segmentStart = cursor;
			int segmentEnd = cursor + segment.text().length();
			cursor = segmentEnd;

			int from = Math.max(start, segmentStart);
			int to = Math.min(end, segmentEnd);
			if (from < to) {
				result += font.getSplitter().stringWidth(FormattedText.of(
						segment.text().substring(from - segmentStart, to - segmentStart),
						segment.style()));
			}

			if (segmentEnd >= end) {
				break;
			}
		}
		return result;
	}

	private static FormattedText slice(NormalizedText normalized, int start, int end) {
		//drop the trailing whitespace the break opportunity attached to this line
		int trimmedEnd = end;
		while (trimmedEnd > start) {
			char last = normalized.plain().charAt(trimmedEnd - 1);
			if (last == ' ' || last == '\r') {
				trimmedEnd--;
			} else {
				break;
			}
		}

		List<FormattedText> parts = new ArrayList<>();
		int cursor = 0;
		for (Segment segment : normalized.segments()) {
			int segmentStart = cursor;
			int segmentEnd = cursor + segment.text().length();
			cursor = segmentEnd;

			int from = Math.max(start, segmentStart);
			int to = Math.min(trimmedEnd, segmentEnd);
			if (from < to) {
				parts.add(FormattedText.of(segment.text().substring(from - segmentStart, to - segmentStart), segment.style()));
			}

			if (segmentEnd >= trimmedEnd) {
				break;
			}
		}

		if (parts.isEmpty()) {
			return FormattedText.EMPTY;
		}

		return parts.size() == 1 ? parts.get(0) : FormattedText.composite(parts);
	}

	private static NormalizedText normalize(FormattedText text, Style initialStyle) {
		StringBuilder plain = new StringBuilder();
		List<Segment> segments = new ArrayList<>();
		StringBuilder current = new StringBuilder();
		Style[] currentStyle = {null};

		//resolve legacy formatting codes and surrogate pairs into plain text plus per-segment styles
		StringDecomposer.iterateFormatted(
				text, initialStyle, (position, style, codepoint) -> {
					if (currentStyle[0] != null && !currentStyle[0].equals(style)) {
						segments.add(new Segment(current.toString(), currentStyle[0]));
						current.setLength(0);
					}

					currentStyle[0] = style;
					String chars = new String(Character.toChars(codepoint));
					current.append(chars);
					plain.append(chars);
					return true;
				});

		if (currentStyle[0] != null && !current.isEmpty()) {
			segments.add(new Segment(current.toString(), currentStyle[0]));
		}

		return new NormalizedText(plain.toString(), List.copyOf(segments));
	}

	private static Locale currentLocale() {
		String code = Minecraft.getInstance().getLanguageManager().getSelected();
		if (code.isEmpty()) {
			return Locale.ROOT;
		}

		String[] parts = code.split("_", 2);
		return parts.length > 1 ? Locale.of(parts[0], parts[1]) : Locale.of(parts[0]);
	}

	private record Segment(String text, Style style) {}

	private record NormalizedText(String plain, List<Segment> segments) {}

	private record Atom(int start, int end, float width, boolean forcedBreak, int next) {}

	private record Line(FormattedText text, boolean isWrapped) {}

	private record CacheKey(NormalizedText text, int maxWidth, Style initialStyle, Locale locale, Font font) {}
}
