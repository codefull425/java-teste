package br.app.veiculos.exception;

import br.app.veiculos.dto.PneuRequest;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ApiExceptionHandlerTest {

	private final ApiExceptionHandler handler = new ApiExceptionHandler();

	@Test
	void handleNotFound() {
		ProblemDetail pd = handler.handleNotFound(new RecursoNaoEncontradoException("não achou"));
		assertEquals(HttpStatus.NOT_FOUND.value(), pd.getStatus());
		assertEquals("não achou", pd.getDetail());
		assertEquals("Recurso não encontrado", pd.getTitle());
	}

	@Test
	void handleBusiness() {
		ProblemDetail pd = handler.handleBusiness(new RegraNegocioException("regra"));
		assertEquals(HttpStatus.CONFLICT.value(), pd.getStatus());
		assertEquals("regra", pd.getDetail());
		assertEquals("Regra de negócio", pd.getTitle());
	}

	@Test
	void handleDataIntegrity() {
		ProblemDetail pd = handler.handleDataIntegrity(new DataIntegrityViolationException("x"));
		assertEquals(HttpStatus.CONFLICT.value(), pd.getStatus());
		assertTrue(pd.getDetail().toString().contains("integridade"));
		assertEquals("Conflito de dados", pd.getTitle());
	}

	@Test
	void handleValidation() {
		MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(new PneuRequest(), "pneu");
		errors.rejectValue("numeroFogo", "NotBlank", "must not be blank");
		when(ex.getBindingResult()).thenReturn(errors);

		ProblemDetail pd = handler.handleValidation(ex);
		assertEquals(HttpStatus.BAD_REQUEST.value(), pd.getStatus());
		assertEquals("Dados inválidos", pd.getTitle());
		assertTrue(pd.getDetail().toString().contains("numeroFogo"));
	}
}
