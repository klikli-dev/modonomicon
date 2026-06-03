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
 * are translated into primitive research mutations. Ingress can grant facts or increment values;
 * research nodes then unlock from those granted facts and value thresholds.
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
     * Starts authoring ingress from the {@code item_crafted} trigger family.
     */
    public ItemCraftedIngress onItemCrafted(Identifier itemId) {
        return new ItemCraftedIngress(this.research, itemId);
    }

    /**
     * Starts authoring ingress from the {@code item_acquired} trigger family.
     */
    public ItemAcquiredIngress onItemAcquired(Identifier itemId) {
        return new ItemAcquiredIngress(this.research, itemId);
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

        /**
         * Declares a new research value and authors a corresponding ingress hook that increments it.
         *
         * The declared value uses the provided value path. The generated hook id is derived by
         * appending {@code _hook} to that value path.
         */
        public ResearchValueRef declareValue(String valuePath, int increment) {
            var valueRef = this.research.value(valuePath);
            this.incrementValue(valuePath + "_hook", valueRef, increment);
            return valueRef;
        }

        /**
         * Authors an explicit ingress hook that increments the given already-declared value.
         */
        public void incrementValue(String hookPath, ResearchValueRef valueRef, int increment) {
            this.research.incrementValueOnEntryViewedOnce(hookPath, this.entryId, valueRef, increment);
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

        /**
         * Declares a new research value and authors a corresponding ingress hook that increments it.
         */
        public ResearchValueRef declareValue(String valuePath, int increment) {
            var valueRef = this.research.value(valuePath);
            this.incrementValue(valuePath + "_hook", valueRef, increment);
            return valueRef;
        }

        /**
         * Authors an explicit ingress hook that increments the given already-declared value.
         */
        public void incrementValue(String hookPath, ResearchValueRef valueRef, int increment) {
            this.research.incrementValueOnAdvancementEarned(hookPath, this.advancementId, valueRef, increment);
        }
    }

    /**
     * Trigger-specific ingress authoring step for {@code item_crafted}.
     */
    public static final class ItemCraftedIngress {
        private final ResearchDataBuilder research;
        private final Identifier itemId;

        /**
         * Creates an item-crafted ingress step bound to one specific item id.
         */
        public ItemCraftedIngress(ResearchDataBuilder research, Identifier itemId) {
            this.research = research;
            this.itemId = itemId;
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
            this.research.grantFactOnItemCrafted(hookPath, this.itemId, factRef);
        }

        /**
         * Declares a new research value and authors a corresponding ingress hook that increments it.
         *
         * The declared value uses the provided value path. The generated hook id is derived by
         * appending {@code _hook} to that value path.
         */
        public ResearchValueRef declareValue(String valuePath, int increment) {
            var valueRef = this.research.value(valuePath);
            this.incrementValue(valuePath + "_hook", valueRef, increment);
            return valueRef;
        }

        /**
         * Authors an explicit ingress hook that increments the given already-declared value.
         */
        public void incrementValue(String hookPath, ResearchValueRef valueRef, int increment) {
            this.research.incrementValueOnItemCrafted(hookPath, this.itemId, valueRef, increment);
        }
    }

    /**
     * Trigger-specific ingress authoring step for {@code item_acquired}.
     */
    public static final class ItemAcquiredIngress {
        private final ResearchDataBuilder research;
        private final Identifier itemId;

        /**
         * Creates an item-acquired ingress step bound to one specific item id.
         */
        public ItemAcquiredIngress(ResearchDataBuilder research, Identifier itemId) {
            this.research = research;
            this.itemId = itemId;
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
            this.research.grantFactOnItemAcquired(hookPath, this.itemId, factRef);
        }

        /**
         * Declares a new research value and authors a corresponding ingress hook that increments it.
         *
         * The declared value uses the provided value path. The generated hook id is derived by
         * appending {@code _hook} to that value path.
         */
        public ResearchValueRef declareValue(String valuePath, int increment) {
            var valueRef = this.research.value(valuePath);
            this.incrementValue(valuePath + "_hook", valueRef, increment);
            return valueRef;
        }

        /**
         * Authors an explicit ingress hook that increments the given already-declared value.
         */
        public void incrementValue(String hookPath, ResearchValueRef valueRef, int increment) {
            this.research.incrementValueOnItemAcquired(hookPath, this.itemId, valueRef, increment);
        }
    }
}
