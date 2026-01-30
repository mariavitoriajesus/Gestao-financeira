package com.beca.financial.user_service.service;


import com.beca.financial.user_service.api.dto.CreateUserRequest;
import com.beca.financial.user_service.api.dto.ImportUserResponse;
import com.beca.financial.user_service.api.dto.UpdateUserRequest;
import com.beca.financial.user_service.api.dto.UserResponse;
import com.beca.financial.user_service.domain.User;
import com.beca.financial.user_service.domain.enums.StatusUsuario;
import com.beca.financial.user_service.domain.enums.TipoPessoa;
import com.beca.financial.user_service.domain.enums.UserRole;
import com.beca.financial.user_service.repository.UserRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse create(CreateUserRequest request) {

        if (request.email() != null && userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already in use");
        }
        if (request.cpfCnpj() != null && userRepository.existsByCpfCnpj(request.cpfCnpj())) {
            throw new IllegalArgumentException("CPF/CNPJ already in use");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setCpfCnpj(request.cpfCnpj());
        user.setTipoPessoa(request.tipoPessoa() != null ? request.tipoPessoa() : TipoPessoa.FISICA);
        user.setPhone(request.phone());
        user.setEndereco(request.endereco());
        user.setStatus(StatusUsuario.ATIVO);
        user.setCreatedAt(LocalDateTime.now());

        User salved = userRepository.save(user);

        user.setRole(UserRole.USER);

        return toResponse(salved);
    }

    @Transactional(readOnly = true)
    public UserResponse findById(UUID id){
        User user = userRepository.getReferenceById(id);
        return toResponse(user);
    }

    public Page<UserResponse> findAll(Boolean onlyActive, Pageable pageable) {
        Page<User> page = (onlyActive != null && onlyActive) ? userRepository.findAllByStatus(StatusUsuario.ATIVO, pageable)
                : userRepository.findAll(pageable);

        return page.map(this::toResponse);
    }

    public UserResponse update(UUID id, UpdateUserRequest request) {
        User user = getOrThrow(id);
        if (request.email() != null && !request.email().equalsIgnoreCase(user.getEmail())) {
            if (userRepository.existsByEmail(request.email())){
                throw new IllegalArgumentException("Email already in use");
            }
            user.setCpfCnpj(request.cpfCnpj());
        }

        if (request.cpfCnpj() != null && !request.cpfCnpj().equals(user.getCpfCnpj())) {
            if (userRepository.existsByCpfCnpj(request.cpfCnpj())) {
                throw new IllegalArgumentException("CPF/CNPJ already in use");
            }
            user.setCpfCnpj(request.cpfCnpj());
        }

        if (request.nome() != null) user.setName(request.nome());
        if (request.password() != null) user.setPassword(request.password());
        if (request.tipoPessoa() != null) user.setTipoPessoa(request.tipoPessoa());
        if (request.phone() != null) user.setPhone(request.phone());
        if (request.endereco() != null) user.setEndereco(request.endereco());

        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    @Transactional
    public void inactive(UUID id){
        User user = getOrThrow(id);
        user.getStatus();
        userRepository.save(user);
    }

    private User getOrThrow(UUID id) {
        return userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("User not found")
        );
    }

    private UserResponse toResponse(User u){
        return new UserResponse(
                u.getId(),
                u.getName(),
                u.getEmail(),
                u.getCpfCnpj(),
                u.getTipoPessoa(),
                u.getPhone(),
                u.getEndereco(),
                u.getStatus(),
                u.getCreatedAt()
        );
    }

    public ImportUserResponse importUser(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File not sent or empty.");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".xlsx")) {
            throw new IllegalArgumentException("Invalid format. Please submit an .xlsx file.");
        }

        int totalRows = 0;
        int imported = 0;
        List<ImportUserResponse.RowError> errors = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)){
            Sheet sheet = workbook.getSheet(String.valueOf(0));

            for (int r = 1; r < sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;

                totalRows++;

                try {
                    String name = getCellAsString(row.getCell(0));
                    String email = getCellAsString(row.getCell(1));
                    String password = getCellAsString(row.getCell(2));
                    String cpfCnpj = getCellAsString(row.getCell(3));
                    String tipoPessoaStr = getCellAsString(row.getCell(4));
                    String phone = getCellAsString(row.getCell(5));
                    String endereco = getCellAsString(row.getCell(6));

                    TipoPessoa tipoPessoa = (tipoPessoaStr == null || tipoPessoaStr.isBlank()) ? TipoPessoa.FISICA :
                            TipoPessoa.valueOf(tipoPessoaStr.trim().toUpperCase());

                    CreateUserRequest req = new CreateUserRequest(
                            name,
                            email,
                            password,
                            cpfCnpj,
                            tipoPessoa,
                            phone,
                            endereco
                    );

                    this.create(req);
                    imported++;
                } catch (Exception e) {
                    errors.add(new ImportUserResponse.RowError(r + 1, e.getMessage() == null ? "Error importing line" : e.getMessage()));
                }
            }

        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to read Excel: " + e.getMessage(), e);
        }

        return new ImportUserResponse(
                totalRows,
                imported,
                totalRows - imported,
                errors
        );
    }

    private String getCellAsString(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.STRING) return cell.getStringCellValue().trim();
        if (cell.getCellType() == CellType.NUMERIC) {
            double v = cell.getNumericCellValue();
            long lv = (long) v;
            return String.valueOf(lv);
        }
        if (cell.getCellType() == CellType.BOOLEAN) return String.valueOf(cell.getBooleanCellValue());
        if (cell.getCellType() == CellType.FORMULA) return cell.getCellFormula();
        return null;
    }


    public byte[] generateImportTemplate() {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()
        ){
            Sheet sheet = workbook.createSheet("users");

            Row header = sheet.createRow(0);
            List<String> columns = List.of(
                    "name",
                    "email",
                    "password",
                    "cpfCnpj",
                    "tipoPessoa",
                    "phone",
                    "endereco"
            );

            for (int i = 0; i < columns.size(); i++) {
                Cell cell = header.createCell(i);
               cell.setCellValue(columns.get(i));
            }

            Row example = sheet.createRow(1);
            example.createCell(0).setCellValue("João Silva");
            example.createCell(1).setCellValue("joao.silva@email.com");
            example.createCell(2).setCellValue("Senha@123");
            example.createCell(3).setCellValue("12345678900");
            example.createCell(4).setCellValue("FISICA");
            example.createCell(5).setCellValue("11999999999");
            example.createCell(6).setCellValue("Rua das Flores, 123");

            for (int i = 0; i < columns.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao gerar planilha modelo", e);
        }
    }
}
