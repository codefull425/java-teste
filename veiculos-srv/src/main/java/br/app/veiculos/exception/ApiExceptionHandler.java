package br.app.veiculos.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(RecursoNaoEncontradoException.class)
	public ProblemDetail handleNotFound(RecursoNaoEncontradoException ex) {
		ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
		pd.setTitle("Recurso não encontrado");
		pd.setType(URI.create("about:blank"));
		return pd;
	}

	@ExceptionHandler(RegraNegocioException.class)
	public ProblemDetail handleBusiness(RegraNegocioException ex) {
		ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
		pd.setTitle("Regra de negócio");
		pd.setType(URI.create("about:blank"));
		return pd;
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ProblemDetail handleDataIntegrity(DataIntegrityViolationException ex) {
		ProblemDetail pd = ProblemDetail.forStatusAndDetail(
				HttpStatus.CONFLICT,
				"Violação de integridade no banco (posição já ocupada, pneu já aplicado ou chave duplicada).");
		pd.setTitle("Conflito de dados");
		pd.setType(URI.create("about:blank"));
		return pd;
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
		String msg = ex.getBindingResult().getFieldErrors().stream()
				.map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
				.collect(Collectors.joining("; "));
		ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, msg);
		pd.setTitle("Dados inválidos");
		pd.setType(URI.create("about:blank"));
		return pd;
	}
}
