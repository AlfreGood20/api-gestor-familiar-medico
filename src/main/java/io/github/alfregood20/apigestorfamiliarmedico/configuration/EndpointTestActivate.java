package io.github.alfregood20.apigestorfamiliarmedico.configuration;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;
import java.util.HashMap;

@RestController
@RequestMapping("/health")
@Hidden
public class EndpointTestActivate {

    @GetMapping
    public ResponseEntity<HashMap<String, Object>> ping(HttpServletRequest request){

        HashMap response = new HashMap();
        response.put("timetamp", LocalTime.now());
        response.put("uri",request.getRequestURI());
        response.put("menssaje", "Ok");
        response.put("status",200);

        return ResponseEntity.ok(response);
    }
}