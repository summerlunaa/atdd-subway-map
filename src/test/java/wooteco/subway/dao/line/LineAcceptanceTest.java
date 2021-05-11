package wooteco.subway.dao.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.controller.response.LineWithAllSectionsResponse;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.dao.fixture.CommonFixture.*;
import static wooteco.subway.dao.fixture.DomainFixture.*;
import static wooteco.subway.dao.fixture.LineAcceptanceTestFixture.*;

@Sql("classpath:tableInit.sql")
public class LineAcceptanceTest extends AcceptanceTest {
    private static final String LINE_URI = "/lines";

    private static final Map<String, String> PARAMS1 =
            createLineRequest("bg-red-600", "1호선", 1L, 2L, 7);
    private static final Map<String, String> PARAMS2 =
            createLineRequest("bg-green-600", "2호선", 1L, 2L, 7);
    private static final Map<String, String> PARAMS_INCORRECT_FORMAT =
            createLineRequest("bg-red-600", "1호쥐", 1L, 2L, 7);
    private static final Map<String, String> PARAMS_SAME_COLOR =
            createLineRequest("bg-red-600", "2호선", 1L, 2L, 7);

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = extractResponseWhenPost(createLineWithSection(STATIONS1), LINE_URI);
        String uri = response.header("Location");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.body().jsonPath().get("id").toString()).isEqualTo(uri.split("/")[2]);
        assertThat(response.body().jsonPath().getString("name")).isEqualTo(LINE_NAME);
        assertThat(response.body().jsonPath().getString("color")).isEqualTo(LINE_COLOR);
    }

    @DisplayName("기존에 존재하는 노선 이름으로 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        extractResponseWhenPost(PARAMS1, LINE_URI);

        // when
        ExtractableResponse<Response> response = extractResponseWhenPost(PARAMS1, LINE_URI);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("기존에 존재하는 색으로 노선을 생성한다.")
    @Test
    void createLineWithDuplicateColor() {
        // given
        extractResponseWhenPost(PARAMS1, LINE_URI);

        // when
        ExtractableResponse<Response> response = extractResponseWhenPost(PARAMS_SAME_COLOR, LINE_URI);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("올바르지 않은 이름으로 노선을 생성한다.")
    @Test
    void createLineWithWrongName() {
        // given
        ExtractableResponse<Response> response = extractResponseWhenPost(PARAMS_INCORRECT_FORMAT, LINE_URI);

        // when - then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("상행 종점, 하행 종점이 모두 같게 하여 노선을 생성한다.")
    void createLineWithSameStations() {

    }

    @Test
    @DisplayName("존재하지 않는 상행 종점역으로 노선을 생성한다.")
    void createLineWithNotFoundUpStation() {
    }

    @Test
    @DisplayName("존재하지 않는 하행 종점역으로 노선을 생성한다.")
    void createLineWithNotFoundDownStation() {
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> createResponse = extractResponseWhenPost(createLineWithSection(STATIONS1), "/lines"); // 노선 등록
        String uri = createResponse.header("Location");

        //when
        ExtractableResponse<Response> response = extractResponseWhenPut(PARAMS2, uri);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("노선을 Id로 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = extractResponseWhenPost(createLineWithSection(STATIONS1), "/lines"); // 노선 등록

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = extractResponseWhenDelete(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
