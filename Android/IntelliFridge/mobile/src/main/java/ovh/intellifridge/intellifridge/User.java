package ovh.intellifridge.intellifridge;

/**
 * @author Francis O. Makokha
 * Objet utilisateur
 *
 */

public class User {
    int userId;
    String userPrenom, userNom, communeNom,mail,genre,langue,apiKey;

    public User(String userPrenom, String userNom, String communeNom, String mail, String genre, String langue, String apiKey, int userId) {
        this.userPrenom = userPrenom;
        this.userNom = userNom;
        this.communeNom = communeNom;
        this.mail = mail;
        this.genre = genre;
        this.langue = langue;
        this.apiKey = apiKey;
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserPrenom() {
        return userPrenom;
    }

    public void setUserPrenom(String userPrenom) {
        this.userPrenom = userPrenom;
    }

    public String getUserNom() {
        return userNom;
    }

    public void setUserNom(String userNom) {
        this.userNom = userNom;
    }

    public String getCommuneNom() {
        return communeNom;
    }

    public void setCommuneNom(String communeNom) {
        this.communeNom = communeNom;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getLangue() {
        return langue;
    }

    public void setLangue(String langue) {
        this.langue = langue;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
