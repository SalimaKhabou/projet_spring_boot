package tn.fst.projet_jee.entities;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

/**
 * Entité représentant un cours créé dans une Classroom.
 * Un cours est associé à une et une seule Classe (relation ManyToOne).
 *
 * Relation bidirectionnelle avec Classe :
 * - CoursClassroom est le côté PROPRIÉTAIRE de la relation OneToMany/ManyToOne.
 * - Il possède la clé étrangère "code_classe" en base de données.
 */
@Entity
@Table(name = "cours_classroom")
public class CoursClassroom {

    /** Identifiant auto-généré avec la stratégie IDENTITY */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCours;

    /**
     * Spécialité du cours.
     * @Enumerated(EnumType.STRING) → stocke la valeur textuelle ("INFORMATIQUE")
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Specialite specialite;

    /** Nom/intitulé du cours (ex: "Programmation C") */
    @Column(nullable = false)
    private String nom;

    /** Nombre d'heures prévu pour ce cours */
    @Column(nullable = false)
    private Integer nbHeures;

    /**
     * Indique si le cours est archivé.
     * false = cours actif | true = cours archivé
     */
    @Column(nullable = false)
    private Boolean archive;

    /**
     * Relation ManyToOne avec Classe.
     * - CoursClassroom possède la clé étrangère vers Classe.
     * - @JoinColumn(name="code_classe") : nom de la colonne FK en base.
     * - @JsonIgnoreProperties : évite la récursion infinie sans tout ignorer.
     *   On ignore uniquement la liste "coursClassrooms" de Classe lors de la sérialisation.
     */
    @ManyToOne
    @JoinColumn(name = "code_classe")
    @JsonIgnoreProperties("coursClassrooms")
    private Classe classe;

    // ==================== Constructeurs ====================

    public CoursClassroom() {}

    public CoursClassroom(Specialite specialite, String nom, Integer nbHeures, Boolean archive) {
        this.specialite = specialite;
        this.nom = nom;
        this.nbHeures = nbHeures;
        this.archive = archive;
    }

    // ==================== Getters & Setters ====================

    public Integer getIdCours() { return idCours; }
    public void setIdCours(Integer idCours) { this.idCours = idCours; }

    public Specialite getSpecialite() { return specialite; }
    public void setSpecialite(Specialite specialite) { this.specialite = specialite; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public Integer getNbHeures() { return nbHeures; }
    public void setNbHeures(Integer nbHeures) { this.nbHeures = nbHeures; }

    public Boolean getArchive() { return archive; }
    public void setArchive(Boolean archive) { this.archive = archive; }

    public Classe getClasse() { return classe; }
    public void setClasse(Classe classe) { this.classe = classe; }

    @Override
    public String toString() {
        return "CoursClassroom{id=" + idCours + ", nom='" + nom + "', specialite=" + specialite
                + ", nbHeures=" + nbHeures + ", archive=" + archive + "}";
    }
}