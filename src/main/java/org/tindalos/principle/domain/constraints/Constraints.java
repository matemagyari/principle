package org.tindalos.principle.domain.constraints;

import java.util.Optional;

/**
 * Represents the complete set of architectural checks and constraints to be analyzed.
 * Contains configuration for layering, third-party dependencies, package coupling metrics,
 * and submodule blueprint validation.
 */
public record Constraints(
        Optional<Layering> layering,
        Optional<ThirdParty> thirdParty,
        Optional<PackageCouplingConstraints> packageCoupling,
        Optional<SubmodulesBlueprint> submodulesBlueprint) {

    /**
     * Builder for creating Constraints instances with a fluent API.
     */
    public static class Builder {
        private Optional<Layering> layering = Optional.empty();
        private Optional<ThirdParty> thirdParty = Optional.empty();
        private Optional<PackageCouplingConstraints> packageCoupling = Optional.empty();
        private Optional<SubmodulesBlueprint> submodulesBlueprint = Optional.empty();

        public Builder layering(Layering layering) {
            this.layering = Optional.ofNullable(layering);
            return this;
        }

        public Builder thirdParty(ThirdParty thirdParty) {
            this.thirdParty = Optional.ofNullable(thirdParty);
            return this;
        }

        public Builder packageCoupling(PackageCouplingConstraints packageCouplingConstraints) {
            this.packageCoupling = Optional.ofNullable(packageCouplingConstraints);
            return this;
        }

        public Builder submodulesBlueprint(SubmodulesBlueprint submodulesBlueprint) {
            this.submodulesBlueprint = Optional.ofNullable(submodulesBlueprint);
            return this;
        }

        public Constraints build() {
            return new Constraints(layering, thirdParty, packageCoupling, submodulesBlueprint);
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

