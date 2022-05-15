package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dao.station.StationMockDao;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.DataNotExistException;
import wooteco.subway.exception.SubwayException;

class StationServiceTest {

    private static final Station STATION = new Station("강남역");

    private final StationMockDao stationMockDao = new StationMockDao();
    private final StationService stationService = new StationService(stationMockDao);

    @AfterEach
    void afterEach() {
        stationMockDao.clear();
    }

    @DisplayName("지하철역을 생성한다.")
    @Test
    void save() {
        stationService.save(STATION);

        assertThat(stationService.findAll()).hasSize(1);
    }

    @DisplayName("중복된 지하철역을 생성할 경우 예외를 발생시킨다.")
    @Test
    void saveDuplicatedName() {
        stationService.save(STATION);

        assertThatThrownBy(() -> stationService.save(STATION))
                .isInstanceOf(SubwayException.class)
                .hasMessage("지하철역 이름이 중복됩니다.");
    }

    @DisplayName("지하철역을 삭제한다.")
    @Test
    void delete() {
        long stationId = stationService.save(STATION);

        assertThatCode(() -> stationService.delete(stationId))
                .doesNotThrowAnyException();
    }

    @DisplayName("존재하지 않는 지하철역을 삭제할 경우 예외가 발생한다.")
    @Test
    void deleteNotExistStation() {
        assertThatThrownBy(() -> stationService.delete(1L))
                .isInstanceOf(DataNotExistException.class)
                .hasMessage("존재하지 않는 지하철역입니다.");
    }
}
