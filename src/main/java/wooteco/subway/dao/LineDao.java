package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;
import wooteco.subway.domain.Line;

public interface LineDao {

    long save(Line line);

    boolean existLineByName(String name);

    boolean existLineByColor(String color);

    List<Line> findAll();

    Optional<Line> findById(Long id);

    int update(Line line);

    int delete(Long id);
}
