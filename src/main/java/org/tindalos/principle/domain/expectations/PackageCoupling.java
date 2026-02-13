package org.tindalos.principle.domain.expectations;

import java.util.Optional;

/**
 * Represents package coupling metrics and expectations for architecture analysis.
 * Encapsulates various coupling-related checks including ADP, SDP, SAP, ACD, RACD, NCCD,
 * and package structure grouping analysis.
 */
public record PackageCoupling(
        Optional<ADP> adp,
        Optional<SDP> sdp,
        Optional<SAP> sap,
        Optional<ACD> acd,
        Optional<RACD> racd,
        Optional<NCCD> nccd,
        Optional<Grouping> grouping) {

    public PackageCoupling {
        adp = adp != null ? adp : Optional.empty();
        sdp = sdp != null ? sdp : Optional.empty();
        sap = sap != null ? sap : Optional.empty();
        acd = acd != null ? acd : Optional.empty();
        racd = racd != null ? racd : Optional.empty();
        nccd = nccd != null ? nccd : Optional.empty();
        grouping = grouping != null ? grouping : Optional.empty();
    }

    /**
     * Factory method to create PackageCoupling with all fields empty.
     *
     * @return a new PackageCoupling with all optional fields empty
     */
    public static PackageCoupling empty() {
        return new PackageCoupling(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );
    }

    /**
     * Builder for creating PackageCoupling instances with a fluent API.
     */
    public static class Builder {
        private Optional<ADP> adp = Optional.empty();
        private Optional<SDP> sdp = Optional.empty();
        private Optional<SAP> sap = Optional.empty();
        private Optional<ACD> acd = Optional.empty();
        private Optional<RACD> racd = Optional.empty();
        private Optional<NCCD> nccd = Optional.empty();
        private Optional<Grouping> grouping = Optional.empty();

        public Builder adp(ADP adp) {
            this.adp = Optional.ofNullable(adp);
            return this;
        }

        public Builder sdp(SDP sdp) {
            this.sdp = Optional.ofNullable(sdp);
            return this;
        }

        public Builder sap(SAP sap) {
            this.sap = Optional.ofNullable(sap);
            return this;
        }

        public Builder acd(ACD acd) {
            this.acd = Optional.ofNullable(acd);
            return this;
        }

        public Builder racd(RACD racd) {
            this.racd = Optional.ofNullable(racd);
            return this;
        }

        public Builder nccd(NCCD nccd) {
            this.nccd = Optional.ofNullable(nccd);
            return this;
        }

        public Builder grouping(Grouping grouping) {
            this.grouping = Optional.ofNullable(grouping);
            return this;
        }

        public PackageCoupling build() {
            return new PackageCoupling(adp, sdp, sap, acd, racd, nccd, grouping);
        }
    }

    /**
     * Creates a new builder for PackageCoupling.
     *
     * @return a new Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }
}

