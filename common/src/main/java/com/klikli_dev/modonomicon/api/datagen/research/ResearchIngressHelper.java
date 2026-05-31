/*
 * SPDX-FileCopyrightText: 2026 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.klikli_dev.modonomicon.api.datagen.research;

import net.minecraft.resources.Identifier;

/**
 * Fluent helper for authoring research ingress.
 *
 * Ingress is the boundary where external events such as book entry views or advancement completion
 * are translated into primitive research mutations. In the current model, ingress grants facts;
 * research nodes then unlock from those granted facts.
 */
public final class ResearchIngressHelper {
    private final ResearchDataBuilder research;

    /**
     * Creates an ingress helper over the given research builder.
     */
    public ResearchIngressHelper(ResearchDataBuilder research) {
        this.research = research;
    }

    /**
     * Starts authoring ingress from the {@code entry_viewed_once} trigger family.
     */
    public EntryViewedOnceIngress onEntryViewedOnce(Identifier entryId) {
        return new EntryViewedOnceIngress(this.research, entryId);
    }

    /**
     * Starts authoring ingress from the advancement-earned trigger family.
     */
    public AdvancementEarnedIngress onAdvancementEarned(Identifier advancementId) {
        return new AdvancementEarnedIngress(this.research, advancementId);
    }

    /**
     * Trigger-specific ingress authoring step for {@code entry_viewed_once}.
     */
    public static final class EntryViewedOnceIngress {
        private final ResearchDataBuilder research;
        private final Identifier entryId;

        /**
         * Creates an entry-viewed ingress step bound to one specific book entry id.
         */
        public EntryViewedOnceIngress(ResearchDataBuilder research, Identifier entryId) {
            this.research = research;
            this.entryId = entryId;
        }

        /**
         * Declares a new research fact and authors a corresponding ingress hook that grants it.
         *
         * The declared fact uses the provided fact path. The generated hook id is derived by
         * appending {@code _hook} to that fact path.
         */
        public ResearchFactRef declareFact(String factPath) {
            var factRef = this.research.fact(factPath);
            this.grantFact(factPath + "_hook", factRef);
            return factRef;
        }

        /**
         * Authors an explicit ingress hook that grants the given already-declared fact.
         */
        public void grantFact(String hookPath, ResearchFactRef factRef) {
            this.research.grantFactOnEntryViewedOnce(hookPath, this.entryId, factRef);
        }
    }

    /**
     * Trigger-specific ingress authoring step for advancement completion.
     */
    public static final class AdvancementEarnedIngress {
        private final ResearchDataBuilder research;
        private final Identifier advancementId;

        /**
         * Creates an advancement-earned ingress step bound to one specific advancement id.
         */
        public AdvancementEarnedIngress(ResearchDataBuilder research, Identifier advancementId) {
            this.research = research;
            this.advancementId = advancementId;
        }

        /**
         * Declares a new research fact and authors a corresponding ingress hook that grants it.
         *
         * The declared fact uses the provided fact path. The generated hook id is derived by
         * appending {@code _hook} to that fact path.
         */
        public ResearchFactRef declareFact(String factPath) {
            var factRef = this.research.fact(factPath);
            this.grantFact(factPath + "_hook", factRef);
            return factRef;
        }

        /**
         * Authors an explicit ingress hook that grants the given already-declared fact.
         */
        public void grantFact(String hookPath, ResearchFactRef factRef) {
            this.research.grantFactOnAdvancementEarned(hookPath, this.advancementId, factRef);
        }
    }
}
