package tn.fst.projet_jee.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
        import java.util.List;

/**
 * Entité représentant un utilisateur de l'application (étudiant, admin, etc.).
 *
 * Relations :
 * - Un utilisateur peut appartenir à plusieurs classes (@ManyToMany).
 *   → Côté "inverse" de la relation bidirectionnelle avec Classe.
 *   → mappedBy="utilisateurs" fait référence au champ dans Classe.
 *   → @JsonIgnore évite la récursion infinie lors de la sérialisation JSON.
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

    /**
     * Relation ManyToMany avec Classe.
     * - mappedBy="utilisateurs" : Classe est le côté propriétaire (possède la table de jointure).
     * - @JsonIgnore : évite la boucle infinie JSON (Utilisateur → Classe → Utilisateur → ...).
     */
    @ManyToMany(mappedBy = "utilisateurs")
    @JsonIgnore
    private List<Classe> classes;

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

    public List<Classe> getClasses() { return classes; }
    public void setClasses(List<Classe> classes) { this.classes = classes; }

    @Override
    public String toString() {
        return "Utilisateur{id=" + idUtilisateur + ", prenom='" + prenom + "', nom='" + nom + "'}";
    }
}