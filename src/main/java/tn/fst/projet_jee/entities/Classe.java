package tn.fst.projet_jee.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

/**
 * Entité représentant une classe (groupe d'étudiants) dans l'établissement.
 *
 * Relations bidirectionnelles :
 * 1. Classe <-> CoursClassroom : OneToMany / ManyToOne
 *    -> Classe peut avoir plusieurs CoursClassroom.
 *    -> Un CoursClassroom n'appartient qu'à une seule Classe.
 */
@Entity
@Table(name = "classe")
public class Classe {

    /** Identifiant auto-généré avec la stratégie IDENTITY */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codeClasse;

    /** Titre/nom de la classe (ex: "4AG1", "5EM1") */
    @Column(nullable = false, unique = true)
    private String titre;

    /**
     * Niveau de la classe (ex: QUATRIEME, CINQUIEME).
     * @Enumerated(EnumType.STRING) -> stocke la valeur textuelle en base ("QUATRIEME")
     * et non l'index ordinal (0, 1, 2...).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Niveau niveau;

    /**
     * Relation ManyToMany avec Utilisateur.
     * - Classe est le côté propriétaire -> elle possède la table de jointure.
     * - @JoinTable définit le nom de la table intermédiaire et ses colonnes.
     * - CORRECTION : cascade = {PERSIST, MERGE} uniquement (pas ALL).
     *   CascadeType.ALL sur ManyToMany est dangereux : un delete de Classe
     *   tenterait de supprimer les Utilisateurs même s'ils appartiennent à d'autres classes.
     * - fetch = LAZY : les utilisateurs ne sont chargés que si on y accède.
     */
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "classe_utilisateur",
            joinColumns = @JoinColumn(name = "codeClasse"),
            inverseJoinColumns = @JoinColumn(name = "idUtilisateur")
    )
    private List<Utilisateur> utilisateurs;

    /**
     * Relation OneToMany avec CoursClassroom.
     * - mappedBy="classe" : CoursClassroom est le côté propriétaire (possède la FK).
     * - @JsonIgnore : évite la boucle infinie JSON (Classe -> CoursClassroom -> Classe -> ...).
     */
    @OneToMany(mappedBy = "classe", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<CoursClassroom> coursClassrooms;

    // ==================== Constructeurs ====================

    public Classe() {}

    public Classe(String titre, Niveau niveau) {
        this.titre = titre;
        this.niveau = niveau;
    }

    // ==================== Getters & Setters ====================

    public Integer getCodeClasse() { return codeClasse; }
    public void setCodeClasse(Integer codeClasse) { this.codeClasse = codeClasse; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public Niveau getNiveau() { return niveau; }
    public void setNiveau(Niveau niveau) { this.niveau = niveau; }

    public List<Utilisateur> getUtilisateurs() { return utilisateurs; }
    public void setUtilisateurs(List<Utilisateur> utilisateurs) { this.utilisateurs = utilisateurs; }

    public List<CoursClassroom> getCoursClassrooms() { return coursClassrooms; }
    public void setCoursClassrooms(List<CoursClassroom> coursClassrooms) { this.coursClassrooms = coursClassrooms; }

    @Override
    public String toString() {
        return "Classe{codeClasse=" + codeClasse + ", titre='" + titre + "', niveau=" + niveau + "}";
    }
}