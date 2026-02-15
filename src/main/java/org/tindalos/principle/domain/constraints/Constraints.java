package org.tindalos.principle.domain.constraints;

import java.util.Optional;

/**
 * Represents the complete set of architectural checks and expectations to be analyzed.
 * Contains configuration for layering, third-party dependencies, package coupling metrics,
 * and submodule blueprint validation.
 */
public record Constraints(
        Layering layering,
        Optional<ThirdParty> thirdParty,
        Optional<PackageCouplingConstraints> packageCoupling,
        Optional<SubmodulesBlueprint> submodulesBlueprint) {

    /**
     * Builder for creating Checks instances with a fluent API.
     */
    public static class Builder {
        private Layering layering = null;
        private Optional<ThirdParty> thirdParty = Optional.empty();
        private Optional<PackageCouplingConstraints> packageCoupling = Optional.empty();
        private Optional<SubmodulesBlueprint> submodulesBlueprint = Optional.empty();

        public Builder layering(Layering layering) {
            this.layering = layering;
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

