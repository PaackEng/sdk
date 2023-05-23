package eu.paack.sdk.api.model.response;

import eu.paack.sdk.model.CoverageCodes;
import eu.paack.sdk.model.CoverageZones;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CheckCoverageResponse {
    private List<CoverageCodes> coverageCodes;
    private List<CoverageZones> coverageZones;
}
