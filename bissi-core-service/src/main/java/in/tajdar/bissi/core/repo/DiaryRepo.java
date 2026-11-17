package in.tajdar.bissi.core.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import in.tajdar.bissi.core.entity.Diary;
import in.tajdar.bissi.core.enums.DiaryStatus;

public interface DiaryRepo extends JpaRepository<Diary, Long> {
	Optional<Diary> findByMemberId(Long memberId);
    Optional<Diary> findByDiaryNumber(String diaryNumber);
    List<Diary> findByStatus(DiaryStatus status);
}
