package io.github.alfregood20.apigestorfamiliarmedico.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400 PARA MANEJAR CUANDO EL USUARIO NO MANDA NADA
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> bodyInvalido(HttpMessageNotReadableException ex, HttpServletRequest request) {

        ResponseException response = ResponseException.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .menssaje("El cuerpo de la petición es obligatorio o tiene un formato JSON inválido")
                .uri(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    // 500 ERROR DESDE EL SERVIDOR
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> manejarErrorInesperado(Exception ex, HttpServletRequest request){

        ResponseException mensaje= ResponseException.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .menssaje("Error en el servidor. ERROR GENERICO: "+ex.getMessage())
                .uri(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.internalServerError().body(mensaje);
    }

    //401 CREDENCIALES INCORRECTAS
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> badCredentials(HttpServletRequest request) {

        ResponseException response = ResponseException.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .menssaje("Correo y/o contrasena incorrectas")
                .uri(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    //400 VALIDACIONES DE FORMULARIOS
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> badRequest(MethodArgumentNotValidException ex, HttpServletRequest request){

        Map<String, String> mensaje = new HashMap<>();
        ex.getFieldErrors().stream()
                .forEach(error -> mensaje.put(error.getField(), error.getDefaultMessage())
        );

        ResponseException response = ResponseException.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .menssaje(mensaje)
                .uri(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    // 403 PARA MANEJAR ACCESO NO AUTORIZADO MANUALMENTE
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> manejarSinPermisoAUnRecurso(AccessDeniedException ex, HttpServletRequest request){

        ResponseException response = ResponseException.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                .menssaje("No tienes permiso para este recurso")
                .uri(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    //404 PARA MANEJAR USERNAME NO ENCONTRADO (CORREO NO ENCONTRADO)
    @ExceptionHandler(UsuarioNoEncontradoException.class)
    public ResponseEntity<?> usernameNotFound(UsuarioNoEncontradoException ex, HttpServletRequest request){

        ResponseException response = ResponseException.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .menssaje("Usuario no encontrado")
                .uri(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // 404 RECURSOS NO ENCONTRADO
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<?> recursoNoEncontrado(RecursoNoEncontradoException ex, HttpServletRequest request){

        ResponseException response = ResponseException.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .menssaje(ex.getMessage())
                .uri(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // 404 PARA MANEJAR RUTAS NO ENCONTRADOS
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<?> rutaNoEnontrado(HttpServletRequest request){

        ResponseException response = ResponseException.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .menssaje("Ruta no encontrado")
                .uri(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    //405 PARA MANEJAR ENDPOINT CON METODO NO SOPORTADO
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> methodNotAllowed(HttpServletRequest request){

        ResponseException response = ResponseException.builder()
                .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                .error(HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase())
                .menssaje("Ruta: "+request.getRequestURL()+" no soporta "+request.getMethod())
                .uri(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }


}
