    package com.demo.persistencia.demopersistencia.controllers;

    import java.time.LocalDate;
    import java.util.List;
    import java.util.Map;

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.web.bind.annotation.*;

    import com.demo.persistencia.demopersistencia.dto.PacienteDTO;
    import com.demo.persistencia.demopersistencia.entidades.Paciente;
    import com.demo.persistencia.demopersistencia.entidades.Usuario;
    import com.demo.persistencia.demopersistencia.repositorio.PacienteRepository;
    import com.demo.persistencia.demopersistencia.repositorio.UsuarioRepository;
    import com.demo.persistencia.demopersistencia.security.JwtUtil;
    import com.demo.persistencia.demopersistencia.services.PacienteService;

    import jakarta.servlet.http.HttpServletRequest;

    @CrossOrigin(origins = "*")
    @RestController
    @RequestMapping("/api/pacientes")
    public class PacienteController {

        @Autowired
        private PacienteService pacienteService;

        @Autowired
        private UsuarioRepository usuarioRepository;

        @Autowired
        private PacienteRepository pacienteRepository;

        // ✅ LISTAR (SOLO ADMIN)
        @GetMapping("/listarPacientes")
        public List<PacienteDTO> consultarPacientes(HttpServletRequest request) {

            String rol = JwtUtil.getRol(request.getHeader("Authorization").substring(7));

            if (!"ADMIN".equals(rol)) {
                throw new RuntimeException("Acceso denegado");
            }

            return pacienteService.consultarPacientes();
        }

        // ✅ BUSCAR (SOLO ADMIN)
        @GetMapping("/buscar")
        public List<Paciente> buscar(@RequestParam String nombre,
                                    HttpServletRequest request) {

            String rol = JwtUtil.getRol(request.getHeader("Authorization").substring(7));

            if (!"ADMIN".equals(rol)) {
                throw new RuntimeException("Acceso denegado");
            }

            return pacienteRepository.findByNombreCompletoContainingIgnoreCase(nombre);
        }

        // ✅ ACTUALIZAR (SOLO ADMIN)
        @PutMapping("/actualizar/{id}")
        public Paciente actualizar(@PathVariable Long id,
                                    @RequestBody Paciente p,
                                    HttpServletRequest request) {

            String rol = JwtUtil.getRol(request.getHeader("Authorization").substring(7));

            if (!"ADMIN".equals(rol)) {
                throw new RuntimeException("Acceso denegado");
            }

            Paciente paciente = pacienteRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

            paciente.setNombreCompleto(p.getNombreCompleto());
            paciente.setTelefono(p.getTelefono());
            paciente.setDireccion(p.getDireccion());
            paciente.setSeguro(p.getSeguro());

            return pacienteRepository.save(paciente);
        }

        // ✅ CREAR PACIENTE + USUARIO (SOLO ADMIN)
        @PostMapping("/crear-con-usuario")
        public Map<String, String> crearConUsuario(@RequestBody Map<String, Object> datos,
                                                HttpServletRequest request) {

            String rol = JwtUtil.getRol(request.getHeader("Authorization").substring(7));

            if (!"ADMIN".equals(rol)) {
                throw new RuntimeException("Solo ADMIN puede crear pacientes");
            }

            Paciente paciente = new Paciente();
            paciente.setNombreCompleto((String) datos.get("nombreCompleto"));
            paciente.setFechaNacimiento(LocalDate.parse((String) datos.get("fechaNacimiento")));
            paciente.setDireccion((String) datos.get("direccion"));
            paciente.setTelefono((String) datos.get("telefono"));
            paciente.setSeguro((String) datos.get("seguro"));

            paciente = pacienteRepository.save(paciente);

            Usuario usuario = new Usuario();
            usuario.setUsername((String) datos.get("username"));
            usuario.setPassword((String) datos.get("password"));
            usuario.setTipoUsuario("PACIENTE");
            usuario.setPacienteId(paciente.getIdPaciente());

            usuarioRepository.save(usuario);

            return Map.of("mensaje", "✅ Paciente creado correctamente");
        }

        // ✅ PERFIL (SOLO PACIENTE)
        @GetMapping("/mi-perfil")
        public Paciente obtenerMiPerfil(HttpServletRequest request) {

            String token = request.getHeader("Authorization").substring(7);
            String username = JwtUtil.getUsername(token);
            String rol = JwtUtil.getRol(token);

            if (!"PACIENTE".equals(rol)) {
                throw new RuntimeException("Acceso denegado");
            }

            Usuario usuario = usuarioRepository.findByUsername(username);

            if (usuario == null || usuario.getPacienteId() == null) {
                throw new RuntimeException("Usuario inválido");
            }

            return pacienteRepository.findById(usuario.getPacienteId())
                    .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        }

        // ✅ REGISTRO PÚBLICO (SIN TOKEN)
        @PostMapping("/registrarPaciente")
        public PacienteDTO registrarPaciente(@RequestBody PacienteDTO pacienteJson) {

            Paciente paciente = pacienteService.registrarPaciente(pacienteJson);

            return new PacienteDTO(
                    paciente.getNombreCompleto(),
                    paciente.getFechaNacimiento(),
                    paciente.getDireccion(),
                    paciente.getTelefono(),
                    paciente.getSeguro()
            );
        }
    }