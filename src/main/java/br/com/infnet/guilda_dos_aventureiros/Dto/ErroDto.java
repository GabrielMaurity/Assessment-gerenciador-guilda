package br.com.infnet.guilda_dos_aventureiros.Dto;

import java.util.List;

public record ErroDto(String mensagem, List<String> detalhes) {}
