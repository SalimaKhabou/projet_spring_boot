package tn.fst.projet_jee.entities;

import jakarta.persistence.*;

/**
 * Entité représentant un utilisateur de l'application (étudiant, admin, etc.).
 *
 * Selon le diagramme de classes :
 * - La relation avec Classe est UNIDIRECTIONNELLE (Utilisateur 1 -> * Classe)
 * - Utilisateur ne possède PAS de collection de classes
 * - C'est Classe qui possède la référence vers Utilisateur (ManyToOne)
 */
@Entity
@Table(name = "utilisateur")
public class Utilisateur {

    /** Identifiant auto-généré avec la stratégie IDENTITY (auto-incrément en MySQL) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUtilisateur;

    /** Prénom de l'utilisateur */
    @Column(nullable = false)
    private String prenom;

    /** Nom de famille de l'utilisateur */
    @Column(nullable = false)
    private String nom;

    /** Mot de passe de l'utilisateur (stocké en clair pour ce TP) */
    @Column(nullable = false)
    private String password;

    // ==================== Constructeurs ====================

    public Utilisateur() {}

    public Utilisateur(String prenom, String nom, String password) {
        this.prenom = prenom;
        this.nom = nom;
        this.password = password;
    }

    // ==================== Getters & Setters ====================

    public Integer getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(Integer idUtilisateur) { this.idUtilisateur = idUtilisateur; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String toString() {
        return "Utilisateur{id=" + idUtilisateur + ", prenom='" + prenom + "', nom='" + nom + "'}";
    }
}