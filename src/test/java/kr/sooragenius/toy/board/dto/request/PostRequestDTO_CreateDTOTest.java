package kr.sooragenius.toy.board.dto.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;


import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PostRequestDTO_CreateDTOTest {
    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void NOT_EMPTY를_갖은_모든_변수가_값을_갖을때는_에러가_없어야한다() {
        // given
        PostRequestDTO.CreateDTO createDTO = PostRequestDTO.CreateDTO.builder().title("TITLE").password("PASSWORD").contents("CONTENTS").build();
        // when
        Set<ConstraintViolation<PostRequestDTO.CreateDTO>> validate = validator.validate(createDTO);
        // then
        assertThat(validate.isEmpty()).isTrue();
    }
    @ParameterizedTest
    @MethodSource(value = "generatePostDTOCreateForValidate")
    public void NOT_EMPTY_체크(PostRequestDTO.CreateDTO createDTO) {
        // when
        Set<ConstraintViolation<PostRequestDTO.CreateDTO>> validate = validator.validate(createDTO);
        // then
        assertThat(validate.isEmpty()).isFalse();
    }

    private static List<PostRequestDTO.CreateDTO> generatePostDTOCreateForValidate() {
        PostRequestDTO.CreateDTO.CreateDTOBuilder createBuilder = PostRequestDTO.CreateDTO.builder().title("TITLE").password("PASSWORD").contents("CONTNTES");
        return Arrays.asList(
                createBuilder.title("").build(),
                createBuilder.password("").build(),
                createBuilder.contents("").build()
        );
    }
}