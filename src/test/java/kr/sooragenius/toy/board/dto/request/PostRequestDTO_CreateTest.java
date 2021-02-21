package kr.sooragenius.toy.board.dto.request;

import kr.sooragenius.toy.board.dto.request.PostRequestDTO;
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

class PostRequestDTO_CreateTest {
    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void NOT_EMPTY를_갖은_모든_변수가_값을_갖을때는_에러가_없어야한다() {
        // given
        PostRequestDTO.Create create = PostRequestDTO.Create.builder().title("TITLE").password("PASSWORD").contents("CONTENTS").build();
        // when
        Set<ConstraintViolation<PostRequestDTO.Create>> validate = validator.validate(create);
        // then
        assertThat(validate.isEmpty()).isTrue();
    }
    @ParameterizedTest
    @MethodSource(value = "generatePostDTOCreateForValidate")
    public void NOT_EMPTY_체크(PostRequestDTO.Create create) {
        // when
        Set<ConstraintViolation<PostRequestDTO.Create>> validate = validator.validate(create);
        // then
        assertThat(validate.isEmpty()).isFalse();
    }

    private static List<PostRequestDTO.Create> generatePostDTOCreateForValidate() {
        PostRequestDTO.Create.CreateBuilder createBuilder = PostRequestDTO.Create.builder().title("TITLE").password("PASSWORD").contents("CONTNTES");
        return Arrays.asList(
                createBuilder.title("").build(),
                createBuilder.password("").build(),
                createBuilder.contents("").build()
        );
    }
}