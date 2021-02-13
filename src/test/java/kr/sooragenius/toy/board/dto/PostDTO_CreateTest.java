package kr.sooragenius.toy.board.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;


import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PostDTO_CreateTest {
    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void NOT_EMPTY를_갖은_모든_변수가_값을_갖을때는_에러가_없어야한다() {
        // given
        PostDTO.Create create = PostDTO.Create.builder().title("TITLE").password("PASSWORD").contents("CONTENTS").build();
        // when
        Set<ConstraintViolation<PostDTO.Create>> validate = validator.validate(create);
        // then
        assertThat(validate.isEmpty()).isTrue();
    }
    @ParameterizedTest
    @MethodSource(value = "generatePostDTOCreateForValidate")
    public void NOT_EMPTY_체크(PostDTO.Create create) {
        // when
        Set<ConstraintViolation<PostDTO.Create>> validate = validator.validate(create);
        // then
        assertThat(validate.isEmpty()).isFalse();
    }

    private static List<PostDTO.Create> generatePostDTOCreateForValidate() {
        return Arrays.asList(
                PostDTO.Create.builder().title("").password("PASSWORD").contents("CONTENTS").build(),
                PostDTO.Create.builder().title("TITLE").password("").contents("CONTENTS").build(),
                PostDTO.Create.builder().title("TITLE").password("PASSWORD").contents("").build()
        );
    }
}