# Padrões para Entidades e DTOs no Projeto REV3RSO

## Modelos (Entidades)

Para as entidades do domínio, usamos Lombok para reduzir código boilerplate, com as seguintes anotações:

### Anotações Lombok Recomendadas para Modelos

- `@Getter` - Gera métodos getters para todos os campos
- `@Setter` - Gera métodos setters para todos os campos
- `@NoArgsConstructor` - Gera construtor sem argumentos (necessário para JPA)
- `@AllArgsConstructor` - Gera construtor com todos os argumentos (opcional)
- `@ToString(exclude = {"campos relacionados"})` - Gera toString(), mas exclui campos que poderiam causar recursão infinita

### Exemplo de Modelo com Lombok
