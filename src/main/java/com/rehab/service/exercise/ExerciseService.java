package com.rehab.service.exercise;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rehab.apiPayload.code.status.ErrorStatus;
import com.rehab.apiPayload.exception.RehabPlanException;
import com.rehab.domain.entity.Exercise;
import com.rehab.domain.entity.ExerciseImage;
import com.rehab.domain.entity.ExerciseMedia;
import com.rehab.dto.exercise.ExerciseDetailResponse;
import com.rehab.domain.repository.exercise.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 운동 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExerciseService {

	private final ExerciseRepository exerciseRepository;
	private final ObjectMapper objectMapper;

	/**
	 * 운동 상세 정보 조회
	 */
	public ExerciseDetailResponse getExerciseDetail(Long exerciseId) {
		log.info("운동 상세 정보 조회 - exerciseId: {}", exerciseId);

		Exercise exercise = exerciseRepository.findByIdWithDetails(exerciseId)
			.orElseThrow(() -> new RehabPlanException(ErrorStatus.EXERCISE_NOT_FOUND));

		return convertToExerciseDetailResponse(exercise);
	}

	/**
	 * Exercise -> ExerciseDetailResponse 변환
	 */
	private ExerciseDetailResponse convertToExerciseDetailResponse(Exercise exercise) {
		List<ExerciseDetailResponse.ExerciseImageResponse> imageResponses =
			exercise.getExerciseImages().stream()
				.map(this::convertToImageResponse)
				.collect(Collectors.toList());

		List<ExerciseDetailResponse.ExerciseMediaResponse> mediaResponses =
			exercise.getExerciseMedias().stream()
				.map(this::convertToMediaResponse)
				.collect(Collectors.toList());

		return ExerciseDetailResponse.builder()
			.exerciseId(exercise.getExerciseId())
			.title(exercise.getTitle())
			.description(exercise.getDescription())
			.bodyPart(exercise.getBodyPart())
			.difficulty(exercise.getDifficulty())
			.contraindications(parseJson(exercise.getContraindications()))
			.progressionRules(parseJson(exercise.getProgressionRules()))
			.evidenceLevel(exercise.getEvidenceLevel())
			.images(imageResponses)
			.media(mediaResponses)
			.createdAt(exercise.getCreatedAt())
			.updatedAt(exercise.getUpdatedAt())
			.build();
	}

	/**
	 * ExerciseImage -> ExerciseImageResponse 변환
	 */
	private ExerciseDetailResponse.ExerciseImageResponse convertToImageResponse(ExerciseImage image) {
		return ExerciseDetailResponse.ExerciseImageResponse.builder()
			.exerciseImageId(image.getExerciseImageId())
			.title(image.getTitle())
			.imageUrl(parseJson(image.getImageUrl()))
			.build();
	}

	/**
	 * ExerciseMedia -> ExerciseMediaResponse 변환
	 */
	private ExerciseDetailResponse.ExerciseMediaResponse convertToMediaResponse(ExerciseMedia media) {
		return ExerciseDetailResponse.ExerciseMediaResponse.builder()
			.exerciseMediaId(media.getExerciseMediaId())
			.url(media.getUrl())
			.mediaType(media.getMediaType())
			.language(media.getLanguage())
			.duration(media.getDuration())
			.build();
	}

	/**
	 * JSON 문자열을 JsonNode로 변환
	 */
	private JsonNode parseJson(String jsonString) {
		if (jsonString == null || jsonString.isEmpty()) {
			return null;
		}
		try {
			return objectMapper.readTree(jsonString);
		} catch (JsonProcessingException e) {
			log.error("JSON 파싱 실패: {}", jsonString, e);
			return null;
		}
	}
}
