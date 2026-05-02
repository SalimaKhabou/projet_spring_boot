package tn.fst.projet_jee.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

/**
 * Entité représentant une classe (groupe d'étudiants) dans l'établissement.
 *
 * Relations selon le diagramme de classes :
 * 1. Utilisateur -> Classe : Relation unidirectionnelle 1 to *
 *    -> Une classe appartient à UN utilisateur (ManyToOne)
 *    -> Un utilisateur peut avoir plusieurs classes (OneToMany du côté Utilisateur non mappé)
 *
 * 2. Classe <-> CoursClassroom : Relation bidirectionnelle OneToMany / ManyToOne
 *    -> Une classe peut avoir plusieurs CoursClassroom
 *    -> Un CoursClassroom n'appartient qu'à une seule Classe
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
     * Relation ManyToOne avec Utilisateur (relation unidirectionnelle).
     * - Classe possède la clé étrangère vers Utilisateur
     * - Un utilisateur peut avoir plusieurs classes (1 -> *)
     * - @JoinColumn définit le nom de la colonne FK en base
     */
    @ManyToOne
    @JoinColumn(name = "id_utilisateur")
    private Utilisateur utilisateur;

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

    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }

    public List<CoursClassroom> getCoursClassrooms() { return coursClassrooms; }
    public void setCoursClassrooms(List<CoursClassroom> coursClassrooms) { this.coursClassrooms = coursClassrooms; }

    @Override
    public String toString() {
        return "Classe{codeClasse=" + codeClasse + ", titre='" + titre + "', niveau=" + niveau + "}";
    }
}