package org.tindalos.guardrails.internal.domain.constraints;

import java.util.Optional;

import org.tindalos.guardrails.internal.domain.constraints.slices.Slices;

/**
 * Represents the complete set of architectural constraints and constraints to be analyzed.
 * Contains configuration for third-party dependencies, package coupling metrics,
 * and slices.
 */
public record Constraints(
        Optional<ThirdParty> thirdParty,
        Optional<PackageCouplingConstraints> packageCoupling,
        Optional<Slices> slices) {

    /**
     * Builder for creating Constraints instances with a fluent API.
     */
    public static class Builder {
        private Optional<ThirdParty> thirdParty = Optional.empty();
        private Optional<PackageCouplingConstraints> packageCoupling = Optional.empty();
        private Optional<Slices> slices = Optional.empty();

        public Builder thirdParty(ThirdParty thirdParty) {
            this.thirdParty = Optional.ofNullable(thirdParty);
            return this;
        }

        public Builder packageCoupling(PackageCouplingConstraints packageCouplingConstraints) {
            this.packageCoupling = Optional.ofNullable(packageCouplingConstraints);
            return this;
        }

        public Builder slices(Slices slices) {
            this.slices = Optional.ofNullable(slices);
            return this;
        }

        public Constraints build() {
            return new Constraints(thirdParty, packageCoupling, slices);
        }
    }

    /**
     * Creates a new builder for Constraints.
     *
     * @return a new Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }
}

