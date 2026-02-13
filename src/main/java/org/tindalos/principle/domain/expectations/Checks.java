package org.tindalos.principle.domain.expectations;

import java.util.Optional;

/**
 * Represents the complete set of architectural checks and expectations to be analyzed.
 * Contains configuration for layering, third-party dependencies, package coupling metrics,
 * and submodule blueprint validation.
 */
public record Checks(
        Layering layering,
        Optional<ThirdParty> thirdParty,
        Optional<PackageCoupling> packageCoupling,
        Optional<SubmodulesBlueprint> submodulesBlueprint) {

    /**
     * Builder for creating Checks instances with a fluent API.
     */
    public static class Builder {
        private Layering layering = null;
        private Optional<ThirdParty> thirdParty = Optional.empty();
        private Optional<PackageCoupling> packageCoupling = Optional.empty();
        private Optional<SubmodulesBlueprint> submodulesBlueprint = Optional.empty();

        public Builder layering(Layering layering) {
            this.layering = layering;
            return this;
        }

        public Builder thirdParty(ThirdParty thirdParty) {
            this.thirdParty = Optional.ofNullable(thirdParty);
            return this;
        }

        public Builder packageCoupling(PackageCoupling packageCoupling) {
            this.packageCoupling = Optional.ofNullable(packageCoupling);
            return this;
        }

        public Builder submodulesBlueprint(SubmodulesBlueprint submodulesBlueprint) {
            this.submodulesBlueprint = Optional.ofNullable(submodulesBlueprint);
            return this;
        }

        public Checks build() {
            return new Checks(layering, thirdParty, packageCoupling, submodulesBlueprint);
        }
    }

    /**
     * Creates a new builder for Checks.
     *
     * @return a new Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }
}

