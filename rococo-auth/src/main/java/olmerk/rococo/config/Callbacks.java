package olmerk.rococo.config;

public interface Callbacks {
  interface Web {
    String login = "/authorized";
    String logout = "/logout";
    String init = "/login";
  }
}
