package com.javaspring.blogapi.config.oauth;

public class ResponseUserInfoGitHubOAuth {
    private String login;
    private String name;
    private String email;

    public ResponseUserInfoGitHubOAuth() {
    }

    public ResponseUserInfoGitHubOAuth(String login, String name, String email) {
        this.login = login;
        this.name = name;
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
