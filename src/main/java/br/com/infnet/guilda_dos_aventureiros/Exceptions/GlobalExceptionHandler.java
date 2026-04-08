package br.com.infnet.guilda_dos_aventureiros.Exceptions;

import br.com.infnet.guilda_dos_aventureiros.Dto.ErroDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroDto> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> detalhes = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(erro -> erro.getField() + " " + erro.getDefaultMessage())
                .toList();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErroDto("Solicitação inválida", detalhes));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErroDto> handleResponseStatusException(ResponseStatusException ex) {
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(new ErroDto(ex.getReason(), List.of()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroDto> handleGenericException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErroDto("Erro interno do servidor", List.of(ex.getMessage())));
    }
}