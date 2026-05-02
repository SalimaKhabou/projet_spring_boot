package tn.fst.projet_jee.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  RELATION AVEC CLASSE — UNIDIRECTIONNELLE                       ║
 * ║                                                                  ║
 * ║  Diagramme UML : Utilisateur (*) ──────► (1) Classe             ║
 * ║                                                                  ║
 * ║  Lecture :                                                       ║
 * ║   • Un UTILISATEUR appartient à UNE seule Classe                ║
 * ║   • Une CLASSE peut avoir PLUSIEURS Utilisateurs                 ║
 * ║   • UNIDIRECTIONNELLE : seul Utilisateur connaît Classe          ║
 * ║     Classe ne possède PAS de List<Utilisateur>                   ║
 * ║                                                                  ║
 * ║  Implémentation JPA :                                            ║
 * ║   → @ManyToOne dans Utilisateur (côté propriétaire + unique)    ║
 * ║   → FK "code_classe" dans la TABLE "utilisateur"                ║
 * ║   → Classe n'a AUCUNE référence vers Utilisateur                ║
 * ╚══════════════════════════════════════════════════════════════════╝
 *
 * Table générée :
 *   utilisateur(id_utilisateur PK, prenom, nom, password, code_classe FK)
 */
@Entity
@Table(name = "utilisateur")
public class Utilisateur {

    /**
     * Clé primaire — stratégie IDENTITY = AUTO_INCREMENT MySQL.
     * Exigence de l'énoncé : "identifiants auto-générés avec la stratégie IDENTITY" ✓
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUtilisateur;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String password;

    /**
     * Relation ManyToOne UNIDIRECTIONNELLE vers Classe.
     *
     * @ManyToOne   → plusieurs Utilisateurs peuvent pointer vers une même Classe
     * @JoinColumn  → génère la colonne FK "code_classe" dans la table "utilisateur"
     *               (code_classe référence le code_classe de la table "classe")
     * fetch = LAZY → la Classe n'est pas chargée automatiquement (performance)
     *
     * @JsonIgnoreProperties({"coursClassrooms"}) :
     *   Lors de la sérialisation JSON, la Classe imbriquée s'affiche
     *   mais sans sa liste coursClassrooms (évite la récursion infinie).
     *   On N'a PAS besoin d'ignorer "utilisateurs" car Classe n'en a pas.
     *
     * Valeur par défaut : null (utilisateur non encore affecté à une classe).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "code_classe")
    @JsonIgnoreProperties({"coursClassrooms"})
    private Classe classe;

    // ── Constructeurs ─────────────────────────────────────────────────────

    public Utilisateur() {}

    public Utilisateur(String prenom, String nom, String password) {
        this.prenom   = prenom;
        this.nom      = nom;
        this.password = password;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────

    public Integer getIdUtilisateur()           { return idUtilisateur; }
    public void    setIdUtilisateur(Integer id) { this.idUtilisateur = id; }

    public String  getPrenom()                  { return prenom; }
    public void    setPrenom(String prenom)     { this.prenom = prenom; }

    public String  getNom()                     { return nom; }
    public void    setNom(String nom)           { this.nom = nom; }

    public String  getPassword()                { return password; }
    public void    setPassword(String password) { this.password = password; }

    public Classe  getClasse()                  { return classe; }
    public void    setClasse(Classe classe)     { this.classe = classe; }

    @Override
    public String toString() {
        return "Utilisateur{id=" + idUtilisateur
                + ", prenom='" + prenom + "'"
                + ", nom='" + nom + "'}";
    }
}