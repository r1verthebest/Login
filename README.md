# <img src="https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Objects/Locked%20with%20Key.png" width="35" /> Login 

<p align="center">
  <img src="https://img.shields.io/badge/Version-1.0.0-blue?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Java-8+-orange?style=for-the-badge&logo=java" />
  <img src="https://img.shields.io/badge/Platform-BungeeCord-red?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Database-MySQL-blue?style=for-the-badge&logo=mysql" />
</p>

---

### <img src="https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Writing%20Utensils/Memo.png" width="25" /> Sobre o Projeto

O **Login** é um plugin de autenticação de alto desempenho desenvolvido especificamente para ambientes **BungeeCord**. Focado em escalabilidade, o plugin realiza todas as operações de banco de dados de forma **assíncrona**, garantindo que o thread principal do seu proxy nunca sofra "lags" ou interrupções durante o login dos jogadores.

### <img src="https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Objects/Gear.png" width="25" /> Funcionalidades Principais

* ⚡ **Performance Assíncrona:** Utiliza `CompletableFuture` para todas as consultas MySQL.
* 🛡️ **Segurança:** Sistema de proteção de IP e armazenamento seguro de credenciais.
* 🔄 **Flexibilidade:** Comando in-game para troca de senha (`/changepass`).
* 📊 **Escalabilidade:** Estrutura preparada para suportar grandes redes de servidores.
* 🚀 **Leveza:** Código limpo e otimizado utilizando **Lombok**.

---

### <img src="https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Objects/Books.png" width="25" /> Dependências e Tecnologias

O projeto foi construído utilizando as seguintes bases:

| Dependência | Versão | Descrição |
| :--- | :--- | :--- |
| **BungeeCord API** | 1.8 | API Principal para o Proxy |
| **Spigot API** | 1.8.8 | Suporte para lógicas específicas de jogo |
| **Lombok** | Latest | Redução de boilerplate (Getters/Setters) |
| **MySQL Driver** | 8.0+ | Conectividade com banco de dados |
| **Maven** | 3.x | Gerenciamento de dependências e build |

---

### <img src="https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Objects/Laptop.png" width="25" /> Como Compilar

Certifique-se de ter o **Maven** instalado em sua máquina e execute:

```bash
# Clonar o repositório
git clone [https://github.com/seu-usuario/Login.git](https://github.com/seu-usuario/Login.git)

# Entrar na pasta
cd Login

# Compilar o projeto
mvn clean package
