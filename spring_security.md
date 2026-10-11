# Spring Security

Spring Security es un marco poderoso y flexible diseñado para proporcionar autenticación, autorización y otras características de seguridad en aplicaciones Java. Su arquitectura está basada en una serie de componentes modulares que interactúan entre sí para garantizar la protección de los recursos de la aplicación.

Esquema de ilustracion de filtro:

![ilustracion](https://careers.edicomgroup.com/wp-content/uploads/2025/10/Slide15-1.jpg)

## Dependencia
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

## Configuraciones

```java
@Configuration
@RequiredArgsConstructor
public class SecurityConfig{

	private final CustomAuthenticationEntryPoint autehnticationEntryPoint;
	private final CustomAccessDeniedHandler accessDeniedHandler;
	private final CustomUserDetailsService userDetailsService;

	@Bean
	public SecurityFilterChain filterChain(httpSecurity http) throws Exception {
		http
			// Desactiva el csrf
			.csrf(csrf -> csrf.disnable())

			//Configuracion de cors
			.cors(cors -> cors 
				.configurationSource(request -> {
					// Creamos una configuracion
					CorsConfiguration cofiguration = new CorsConfiguration();

					//Permite enviar credenciales en la petición como cookie o tokens
					configuration.setAllowCredentials(true);

					// Definir las rutas donde se usara esta api
					configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:5173"));

					// Definir que metodos http podra usar por defecto todos
					configuration.setAllowedMethods(Arrays.asList("*"));

					// Definir que cabezara podra usar por defecto todos:
					configuration.setAllowedHeaders(Arrays.asList("*"));

					return configuration;
				})
			)

			// Tipos de estados
			// - STATELESS -> Sin estado
			// - IF_REQUIRED -> Con estado
            .sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

			// Desactivar http basico
			.httpBasic(httpBasic -> httpBasic.disable())

			// Manejar error 403 y 401 personalizado
			.exceptionHandling(exh -> exh
				.accessDeniedHandler(accessDeniedHandler)
				.authenticationEntryPoint(authenticationEntryPoint)
			)

			// Busqueda personalizada de usuarios en base de datos en autenticacion
			.userDetailsService(userDetailsService)

			// Manejo de autorizacion y roles
			.authorizeHttpRequests(requests -> requests

				//El uso de hasRole se usa para indicar los roles de una ruta
				//Puede tener muchos roles

				.requestMatchers("/user/**").hasAnyRole('ROLE_USER','ROLE_ADMIN')
				
				//Solo un rol puede tener
				.requestMatchers("/admin/**").hasRole('ROLE_ADMIN')
				
				//Ruta public para cualquiera
				.requestMatchers("/public/**").permitAll()
				
				//Nadien puede entrar
				.requestMatchers("/documents-v1").denyAll()

				// Si esta autenticado no podra ingresar a esta ruta
				.requestMatchers("/register").anonymous()

				// El uso de hasAuthority se usa para diferentes permisos de una ruta
				.requestMathers(HttpMethod.GET,"users/**").hasAuthority('READ')
				.requestMathers(HttpMethod.POST,"users/").hasAuthority('WRITE')
				.requestMathers(HttpMethod.PUT, "users/**").hasAuthority('UPDATE')
				.requestMathers(HttpMethod.DELETE, "users/**").hasAuthority('DELETE')

				// Igual puede tener muchos persmisos una ruta
				.requestMathers("/Reports").hasAnyAuthority('READ','IMPORT')

				//Las demas rutas necesitara autenticacion
				.anyRequest().authenticated()
			)
			
			;

		return http.build();
	}
}
```

## Manejo de erres 401 y 403

### Error 401 (Recurso no autorizado)
La interfaz AuthenticationEntryPoint es la responsable de manejar los intentos de acceso no autenticados, decidiendo cómo responder cuando alguien accede a la API sin autenticación. Para crear una implementación personalizada de AuthenticationEntryPoint es necesario sobrescribir el método commence().

```java
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
		// LOGICA Y MENSAJE DE 401
	}
}
```

### Error 403 (Recurso no permitido)
AccessDeniedHandler es una interfaz en Spring Security que se utiliza para manejar situaciones en las que un usuario intenta acceder a un recurso protegido sin los permisos necesarios. Cuando ocurre un error de acceso denegado (403 Forbidden), esta interfaz permite personalizar la respuesta que se envía al cliente. En este caso, un AccessDeniedHandler personalizado puede crearse sobrescribiendo el método handle() y configurarse utilizando exceptionHandling().

```java
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
	
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
		// LOGICA Y MENSAJE 403
	}
}
```

## Personalizacion de busqueda de usuario

Cuando un AuthenticationProvider recibe el objeto Authentication, su primera tarea es cargar los detalles del usuario basándose en el nombre de usuario proporcionado. Para lograr esto, el AuthenticationProvider se basa en una implementación de UserDetailsService o UserDetailsManager. Estos componentes son responsables de recuperar información del usuario, como el nombre de usuario, contraseña y roles, desde un sistema de almacenamiento como una base de datos o almacenamiento en memoria. Más específicamente, la interfaz UserDetailsService define un único método abstracto responsable de cargar los detalles del usuario desde un sistema de almacenamiento, mientras que la interfaz UserDetailsManager, que extiende UserDetailsService, introduce métodos adicionales como `createUser()`, `updateUser()`, `deleteUser()`, `changePassword()` y `userExists()`. La separación entre interfaces existe para proporcionar flexibilidad. 

```java
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService{

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername (String username) throws UsernameNotFoundException {
		final User user = userRepository.findByUsername(username).
			.orElseThrows(()-> new UsernameNotFoundException("User not found"));

		UserDetails userFound = new UserDetails(user);

		return userFound;
	}
}
```

## Encriptacion de contraseñas
En Spring Security, las contraseñas nunca deben guardarse en texto plano. La mejor práctica es utilizar un algoritmo hash unidireccional (como BCrypt) que genera una huella única.

```java
@Bean
public PasswordEncoder passwordEncoder(){
	return new BCryptPasswordEncoder();
}
```
### Encriptar
```java
String encodedPassword = passwordEncoder.encode(user.getPassword());
```

### Desencriptar 
```java
// Pondras como parametro la contraseña recibida por el usuario y la contraseña que tiene guardada encriptada
boolean isEquals = passwordEncoder.matches(passwordRequest, passwordDb);
```

## AuthenticationManager
El Authentication Manager es una interfaz en Spring Security que proporciona un mecanismo para autenticar a un usuario en la aplicación. Es responsable de tomar las credenciales del usuario, como el nombre de usuario y la contraseña, y validarlas para determinar si el usuario está autorizado para acceder a los recursos protegidos por la aplicación.

El Authentication Manager se utiliza en el proceso de autenticación de Spring Security. Cuando un usuario intenta acceder a un recurso protegido, Spring Security intercepta la solicitud y comprueba si el usuario está autenticado. Si el usuario no está autenticado, se le redirige al proceso de autenticación, donde el Authentication Manager intenta autenticar al usuario utilizando las credenciales proporcionadas.

```java
@Bean
public AuthenticationManager authenticationManager (AuthenticationConfiguration configuration) throws Exception{
	return configuration.getAuthenticationManager();
}
```

## AuthenticationProvider
AuthenticationProvider es una interfaz en Spring Security que se utiliza para autenticar a un usuario en la aplicación. Es una parte clave del proceso de autenticación en Spring Security y permite a los desarrolladores personalizar el proceso de autenticación para satisfacer las necesidades específicas de su aplicación.

AuthenticationProvider se utiliza típicamente en conjunto con UserDetailsService y PasswordEncoder en Spring Security. UserDetailsService se utiliza para cargar los detalles del usuario desde la base de datos, incluyendo su nombre de usuario y contraseña cifrada. PasswordEncoder se utiliza para cifrar la contraseña proporcionada por el usuario durante el proceso de autenticación. AuthenticationProvider utiliza esta información para autenticar al usuario.

```java
@Bean
public AuthenticationProvider authenticationProvider() {
    // Usamos el anterior UserDetailsService y el PasswordEncoder
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService());
    authProvider.setPasswordEncoder(passwordEncoder());

    return authProvider;
}
```

## Authentication
La autenticación en ⁠Spring Security es el proceso de verificar la identidad de un usuario. Funciona mediante un AuthenticationManager que procesa un objeto Authentication utilizando UserDetailsService para cargar los datos del usuario desde una base de datos o memoria, y un PasswordEncoder para validar de forma segura la contraseña.

```java
Authentication authentication = authenticationManager.authenticate(
    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
);
```

# JWT(Json web token)

JSON Web Token (JWT) es un estándar abierto (RFC 7519) que define una forma compacta y autónoma de transmitir información de forma segura entre partes como un objeto JSON. Esta información puede verificarse y confiarse porque está firmada digitalmente. Los JWT se pueden firmar utilizando un secreto (con el algoritmo HMAC) o un par de claves públicas/privadas utilizando RSA o ECDSA.

![jwt](https://substack-post-media.s3.amazonaws.com/public/images/b48f198f-b0bd-420e-b15a-9e6276acba69_2912x2096.png)

## Estructura jwt
En su forma compacta, los tokens web JSON constan de tres partes separadas por puntos (.), que son:

- Encabezado
- Carga útil
- Firma
Por lo tanto, un JWT normalmente se parece al siguiente:

```jwt
xxxxx.yyyyy.zzzzz
```

### Encabezado (Header)
El encabezado normalmente consta de dos partes: el tipo de token, que es JWT, y el algoritmo de firma que se utiliza, como HMAC SHA256 o RSA.

Por ejemplo:

```jwt
{
  "alg": "HS256",
  "typ": "JWT"
}
```

### Carga útil (Payload)
La segunda parte del token es la carga útil, que contiene las reclamaciones. Las reclamaciones son declaraciones sobre una entidad (normalmente, el usuario) y datos adicionales. Hay tres tipos de reclamaciones: reclamaciones registradas, públicas y privadas.

Por ejemplo:
```jwt
{
  "sub": "1234567890",
  "name": "John Doe",
  "admin": true
}
```

### Firma (Signature)
Para crear la parte de firma, debes tomar el encabezado codificado, la carga útil codificada, un secreto, el algoritmo especificado en el encabezado y firmarlo.

Por ejemplo, si desea utilizar el algoritmo HMAC SHA256, la firma se creará de la siguiente manera:
```jwt
HMACSHA256(
  base64UrlEncode(header) + "." +
  base64UrlEncode(payload),
  secret)
```

## Ciclo de vida de un token
![cicloVida](https://miro.medium.com/v2/resize:fit:720/format:webp/1*t8jU-RdEIjYHQlRbgDDnAA.png)

## Spring security + JWT

### Dependencias
```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>

<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>

<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
```

### Implementación
Antes de comenzar a crear tokens, primero tenemos que definir la tiempo de expiracion y una llave secreta de 32 caracteres que este trabajara como la firma secreta.

Estos datos los debes de guardar en tu application.properties. Te dejo un ejemplo:
```properties
# El tiempo de expiracion se trabaja con milisegundos
# Ejmplo 5 minutos a milisegundos
jwt.expiration = 300000

# La llave secreta debe tener 32 o más caracteres
# ¿Por que debe tener 32 o mas caracteres?
# R= Asi se definera por que usaremos el algoritmo H256
jwt.secret = oj184xZRfvPjH9hiUGTWs7sk9gDN3WPJ
```

#### JwtService
Una vez ya definido los anteriores datos, comenzare con el servicio donde este creara el token, validara si esta se encuentra expirada o si la firma no coinciden entre otros metodos que nos ayudara para su implementación.

```java
@Service
public class JWtService{

	// Agregaremos los datos ya mencionados
	// Con ayuda de la decoracion @Value obtendre esos datos
	@Value("${jwt.secret}")
	private final String secret;

	@Value("${jwt.expirate}")
	private final Long expirate;

	// Aqui genera un token pasando como argumento un objecto usuario
	public String generateToken(CustomUserDetails user){
		return Jwts.builder()
			.claims(generateClaims(user))
			.subject(user.getUsername()) // Como identificaras a un usuario (correo, id, etc)
			.issuedAt(new Date()) // Fecha de emision del token el dia que se crea
			.expiration(new Date(System..currentTimeMillis() + expirate)) // Fecha en la que se va expirar
			.signWith(getKey(), Jwts.SIG.HS256) // Y finalmente la firma y el algoritmo a utilizar
			.compact();
	}

	// Aqui crearas el payload de un token osea que datos quieres que esten en el claims
	private Map<String, Object> generateClaims(CustomUserDetails user){
		var payload = new HashMap<String, Object>();
		payload.put("name",user.getName());
		payload.put("roles",user.getAuthorities());

		return payload;
	}

	// Hay que transformar la clave secreta a un una key valido jwt
	private SecretKey getKey(){
		return Keys.hmacShaKeyFor(secret.getBytes());
	}

	// Esta funcion nos ayudara a obtener el payload de un token
	// Nos servira para poder validar tokens ya que el claims cuenta con datos importantes
	private Claims getClaims(String token){
		return Jwts.Parser()
			.verifyWith(getKey()) // Ingresamos la firma
			.build() // Construimos el validador
			.parseSignedClaims(token) // Lee el token
			.getPayload(); // Finalmente obtenemos el payload
	}

	// Mediante este funcion obtendras el subject (como se identificara un usuario)
	public String getSuject(String token){
		return getClaims(token).getSubject();
	}

	// Para vereficar si el token esta expirado
	private boolean isExpirate(String token){
		return getClaims().getExpirate().before(new Date);
	}

	// Debemos de validar token
	// Ciclo de debe tener
	// 1. Recibir token y usuario
	// 2. Obtener suject del token
	// 3. Hay que verificar si el suject del token es igual al suject de usuario (correo, id, etc)
	// 4. Por ultimo verificar si dicho token no esta expirado y
	public boolean tokenIsValid(String token, CustomUserDetails user){
		final String username = getClaims(token).getSuject();

		return user.getUsername.isEquals(username)  && !isExpirate(token);
	}
}
```

#### Filtro JWT

```java
@Component
@RequiredArgsConstructor
@Slf4j
public class FilterJwt extends OncePerRequestFilter{

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String exError = "JWT_ERROR";

        try{

            final String token = getToken(request);
            log.info("Token recibido: "+token);

            if(token == null){
                request.setAttribute(exError, "Token no recibido");
            }else{

                final String correo = jwtService.obtenerSuject(token);

                if(correo != null && SecurityContextHolder.getContext().getAuthentication() == null){
                    CustomUserDetails usuario = (CustomUserDetails) userDetailsService.loadUserByUsername(correo);

                    if(usuario.getUsername() == correo && jwtService.esValidoToken(token, usuario)){
                        Authentication authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.info("Guardamos con exito al usuario en el contexto de spring security");
                    }
                }
            }

        }catch(Exception ex){

        }finally{
            filterChain.doFilter(request, response);
        }
    }

    private String getToken(HttpServletRequest request){
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(authorization != null && authorization.startsWith("Bearer ")){
            return authorization.substring(7);
        }

        return null;
    }
}
```

