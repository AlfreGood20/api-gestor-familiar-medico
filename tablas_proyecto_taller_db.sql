CREATE DATABASE gestor_familia_medico;

USE gestor_familia_medico;


CREATE TABLE familias (
	id_familia BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(255) NULL,
    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE usuarios (
	id_usuario BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_familia BIGINT NOT NULL,
    nombre_completo VARCHAR(200) NOT NULL,
    correo VARCHAR(255) NOT NULL UNIQUE,
    telefono VARCHAR(20) NULL,
    contrasena_hash VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL,
    estado VARCHAR(15) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_usuarios_rol CHECK (rol IN ('ADMIN','CUIDADOR')),
    CONSTRAINT chk_usuarios_estado CHECK (estado IN ('ACTIVO','INACTIVO')),
    FOREIGN KEY (id_familia) REFERENCES familias(id_familia)
);

CREATE TABLE integrantes (
	id_integrante BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_familia BIGINT NOT NULL,
    id_usuario BIGINT NULL UNIQUE,
    nombre VARCHAR(255) NOT NULL,
    apellido_paterno VARCHAR(255) NULL,
    apellido_materno VARCHAR(255) NULL,
    fecha_nacimiento DATE NOT NULL,
    sexo VARCHAR(15) NOT NULL,
    tipo_sangre VARCHAR(20) NOT NULL DEFAULT 'SIN_DEFINIR',
    parentesco VARCHAR(20) NOT NULL,
    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_integrantes_sexo CHECK (sexo IN ('MASCULINO','FEMENINO')),
    CONSTRAINT chk_integrantes_tipo_sangre CHECK 
		(tipo_sangre IN (
			'A_POSITIVO','A_NEGATIVO',
			'B_POSITIVO','B_NEGATIVO',
            'AB_POSITIVO','AB_NEGATIVO',
            'O_POSITIVO','O_NEGATIVO','SIN_DEFINIR')
	),
    CONSTRAINT chk_integrantes_parentesco CHECK 
		(parentesco IN 
			('PADRE','MADRE','HIJO','HIJA','HERMANO','HERMANA',
			'ABUELO','ABUELA','NIETO','NIETA','TIO','TIA','PRIMO','PRIMA','OTRO')
	),
    
    FOREIGN KEY (id_familia) REFERENCES familias(id_familia),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);

CREATE TABLE alertas (
	id_alerta BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_familia BIGINT NOT NULL,
    id_integrante BIGINT NULL,
    tipo VARCHAR(20) NOT NULL,
    mensaje VARCHAR(255) NOT NULL,
    leida BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_generacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_alertas_tipo CHECK 
		(tipo IN ('CADUCIDAD','STOCK_BAJO','TOMA_PENDIENTE','CONSULTA_PROXIMA','VACUNA_PROXIMA')),
        
	FOREIGN KEY (id_familia) REFERENCES familias(id_familia),
    FOREIGN KEY (id_integrante) REFERENCES integrantes(id_integrante)
);

CREATE TABLE alergias (
	id_alergia BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL UNIQUE,
    tipo VARCHAR(20) NOT NULL DEFAULT 'SIN_DEFINIR',
    
    CONSTRAINT chk_alergias_tipo CHECK 
		(tipo IN ('MEDICAMENTO','ALIMENTO','AMBIENTAL','CONTACTO','INSECTO','OTRA','SIN_DEFINIR'))
);

CREATE TABLE integrante_alergias (
	id_integrante BIGINT NOT NULL,
    id_alergia BIGINT NOT NULL,
    severidad VARCHAR(20) NOT NULL,
    observaciones VARCHAR(255) NULL,
    
    CONSTRAINT chk_integrante_alergias_severidad CHECK (severidad IN ('LEVE','MODERADA','GRAVE')),
    PRIMARY KEY (id_integrante, id_alergia),
    FOREIGN KEY (id_integrante) REFERENCES integrantes(id_integrante),
    FOREIGN KEY (id_alergia) REFERENCES alergias(id_alergia)
);

CREATE TABLE medicamentos (
	id_medicamento BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_comercial VARCHAR(255) NOT NULL,
    sustancia_activa VARCHAR(255) NOT NULL,
    concentracion VARCHAR(255) NOT NULL,
    forma_farmaceutica VARCHAR(20) NOT NULL,
    via_administracion VARCHAR(20) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    
    CONSTRAINT chk_medicamentos_forma_farmaceutica CHECK 
		(forma_farmaceutica IN 
			('TABLETA','CAPSULA','JARABE','SUSPENSION','GOTAS',
			'INYECCION','CREMA','POMADA','INHALADOR','SUPOSITORIO',
            'PARCHE','OTRA')
	),
    CONSTRAINT chk_medicamentos_via_administracion CHECK 
		(via_administracion IN 
			('ORAL','TOPICA','INYECTABLE','OFTALMICA','OTICA',
			'NASAL','INHALADA','RECTAL','SUBLINGUAL','OTRA')
	),
    CONSTRAINT uq_medicamentos UNIQUE 
		(nombre_comercial, concentracion, forma_farmaceutica)
);

CREATE TABLE stock_medicamentos (
	id_stock BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_familia BIGINT NOT NULL,
    id_medicamento BIGINT NOT NULL,
    cantidad INT NOT NULL DEFAULT 0,
    cantidad_minima INT NOT NULL DEFAULT 0,
    unidad VARCHAR(20) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE',
    fecha_caducidad DATE NOT NULL,
    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_stock_cantidad CHECK (cantidad >= 0),
    CONSTRAINT chk_stock_cantidad_minima CHECK (cantidad_minima >= 0),
    CONSTRAINT chk_stock_unidad CHECK (unidad IN (
        'TABLETAS','CAPSULAS','ML','GRAMOS','DOSIS','PIEZAS','OTRA')),
    CONSTRAINT chk_stock_estado CHECK (estado IN (
        'DISPONIBLE','AGOTADO','CADUCADO','DESECHADO')),

	FOREIGN KEY (id_familia) REFERENCES familias(id_familia),
    FOREIGN KEY (id_medicamento) REFERENCES medicamentos(id_medicamento)
);

CREATE TABLE especialidades (
	id_especialidad BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE medicos (
	id_medico BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_familia BIGINT NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    apellido_paterno VARCHAR(255) NULL,
    apellido_materno VARCHAR(255) NULL,
    correo VARCHAR(255) NULL,
    telefono VARCHAR(255) NOT NULL,
    
    FOREIGN KEY (id_familia) REFERENCES familias(id_familia)
);

CREATE TABLE medico_especialidades (
	id_medico BIGINT NOT NULL,
    id_especialidad BIGINT NOT NULL,
    
    PRIMARY KEY (id_medico, id_especialidad),
    FOREIGN KEY (id_medico) REFERENCES medicos(id_medico),
    FOREIGN KEY (id_especialidad) REFERENCES especialidades(id_especialidad)
);

CREATE TABLE consultas (
	id_consulta BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_integrante BIGINT NOT NULL,
    id_medico BIGINT NOT NULL,
    fecha_consulta DATETIME NOT NULL,
    motivo VARCHAR(255) NOT NULL,
    diagnostico TEXT NULL,
    notas TEXT NULL,
    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (id_integrante) REFERENCES integrantes(id_integrante),
    FOREIGN KEY (id_medico) REFERENCES medicos(id_medico)
);

CREATE TABLE vacunas (
	id_vacuna BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    descripcion VARCHAR(255) NULL,
    enfermedad_previene VARCHAR(255) NOT NULL,
    dosis_totales INT NOT NULL DEFAULT 1,
    
    CONSTRAINT chk_vacunas_dosis_totales CHECK (dosis_totales > 0)
);

CREATE TABLE esquemas_vacunacion (
    id_esquema BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_vacuna BIGINT NOT NULL,
    numero_dosis INT NOT NULL,
    edad_recomendada_meses INT NOT NULL,

    CONSTRAINT chk_esquemas_numero_dosis CHECK (numero_dosis > 0),
    CONSTRAINT chk_esquemas_edad CHECK (edad_recomendada_meses >= 0),
    CONSTRAINT uq_esquemas_vacuna_dosis UNIQUE (id_vacuna, numero_dosis),

    FOREIGN KEY (id_vacuna) REFERENCES vacunas(id_vacuna)
);

CREATE TABLE aplicaciones_vacuna (
    id_aplicacion BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_integrante BIGINT NOT NULL,
    id_vacuna BIGINT NOT NULL,
    numero_dosis INT NOT NULL,
    fecha_aplicacion DATE NOT NULL,
    lote VARCHAR(100) NULL,
    lugar_aplicacion VARCHAR(200) NULL,
    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_aplicaciones_numero_dosis CHECK (numero_dosis > 0),
    CONSTRAINT uq_aplicaciones_integrante_vacuna_dosis
        UNIQUE (id_integrante, id_vacuna, numero_dosis),

	FOREIGN KEY (id_integrante) REFERENCES integrantes(id_integrante),
	FOREIGN KEY (id_vacuna) REFERENCES vacunas(id_vacuna)
);

CREATE TABLE estudios_laboratorio (
    id_estudio BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_integrante BIGINT NOT NULL,
    id_consulta BIGINT NULL,
    fecha_estudio DATE NOT NULL,
    tipo_estudio VARCHAR(150) NOT NULL,
    categoria VARCHAR(20) NOT NULL,
    laboratorio VARCHAR(200) NULL,
    resultado TEXT NULL,
    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_estudios_categoria CHECK (categoria IN (
        'SANGRE','ORINA','HECES','IMAGEN','OTRO')),

	FOREIGN KEY (id_integrante) REFERENCES integrantes(id_integrante),
	FOREIGN KEY (id_consulta) REFERENCES consultas(id_consulta)
);

CREATE TABLE recetas (
    id_receta BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_integrante BIGINT NOT NULL,
    id_consulta BIGINT NULL,
    fecha_emision DATE NOT NULL,
    indicaciones TEXT NULL,
    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

	FOREIGN KEY (id_integrante) REFERENCES integrantes(id_integrante),
	FOREIGN KEY (id_consulta) REFERENCES consultas(id_consulta)
);

CREATE TABLE detalles_receta (
    id_detalle BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_receta BIGINT NOT NULL,
    id_medicamento BIGINT NOT NULL,
    dosis VARCHAR(100) NOT NULL,
    frecuencia_horas INT NOT NULL,
    duracion_dias INT NULL,
    fecha_hora_inicio DATETIME NOT NULL,

    CONSTRAINT chk_detalles_frecuencia CHECK (frecuencia_horas > 0),
    CONSTRAINT chk_detalles_duracion CHECK (duracion_dias IS NULL OR duracion_dias > 0),

	FOREIGN KEY (id_receta) REFERENCES recetas(id_receta),
	FOREIGN KEY (id_medicamento) REFERENCES medicamentos(id_medicamento)
);

CREATE TABLE tomas_programadas (
    id_toma BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_detalle BIGINT NOT NULL,
    fecha_hora_programada DATETIME NOT NULL,
    estado VARCHAR(15) NOT NULL DEFAULT 'PENDIENTE',
    fecha_hora_real DATETIME NULL,

    CONSTRAINT chk_tomas_estado CHECK (estado IN ('PENDIENTE','TOMADA','OMITIDA','CANCELADA')),
    CONSTRAINT chk_tomas_real CHECK (estado <> 'TOMADA' OR fecha_hora_real IS NOT NULL),
    CONSTRAINT uq_tomas_detalle_hora UNIQUE (id_detalle, fecha_hora_programada),

	FOREIGN KEY (id_detalle) REFERENCES detalles_receta(id_detalle)
);

SHOW TABLES;