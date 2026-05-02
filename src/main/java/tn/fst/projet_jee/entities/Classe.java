package tn.fst.projet_jee.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  RELATIONS DE CETTE ENTITÉ                                       ║
 * ║                                                                  ║
 * ║  1) Avec Utilisateur — UNIDIRECTIONNELLE                        ║
 * ║     Utilisateur (*) ──────► (1) Classe                          ║
 * ║     → Classe NE connaît PAS ses Utilisateurs                    ║
 * ║     → Classe n'a AUCUN champ List<Utilisateur>                  ║
 * ║     → C'est Utilisateur qui possède la FK "code_classe"         ║
 * ║                                                                  ║
 * ║  2) Avec CoursClassroom — BIDIRECTIONNELLE                      ║
 * ║     Classe (1) ◄──────► (*) CoursClassroom                      ║
 * ║     → Classe côté INVERSE : @OneToMany(mappedBy="classe")       ║
 * ║     → CoursClassroom côté PROPRIÉTAIRE : @ManyToOne + @JoinCol  ║
 * ║                                                                  ║
 * ║  Énumération Niveau :                                            ║
 * ║     @Enumerated(EnumType.STRING) → stocke "QUATRIEME" en base    ║
 * ╚══════════════════════════════════════════════════════════════════╝
 *
 * Table générée :
 *   classe(code_classe PK, titre UNIQUE, niveau VARCHAR)
 *   Aucune FK vers utilisateur dans cette table ✓
 */
@Entity
@Table(name = "classe")
public class Classe {

    /**
     * Clé primaire — stratégie IDENTITY = AUTO_INCREMENT MySQL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codeClasse;

    /** Titre de la classe, ex : "4AG1", "5EM1". Unique en base. */
    @Column(nullable = false, unique = true)
    private String titre;

    /**
     * Niveau de la classe.
     *
     * @Enumerated(EnumType.STRING)
     *   ✓ Stocke la valeur textuelle "QUATRIEME" (pas l'index ordinal 3)
     *   ✓ La base de données contiendra des chaînes lisibles
     *   Sans cette annotation → Hibernate stockerait 0, 1, 2, 3, 4 (illisible)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Niveau niveau;

    /**
     * ══ RELATION BIDIRECTIONNELLE avec CoursClassroom ══
     *
     * @OneToMany  → une Classe peut avoir PLUSIEURS CoursClassroom
     * mappedBy="classe" → CoursClassroom.classe est le côté PROPRIÉTAIRE
     *                      (c'est lui qui possède la FK "code_classe")
     *                      Hibernate ne crée PAS de FK supplémentaire ici.
     * cascade = ALL  → si on supprime une Classe, ses CoursClassroom sont supprimés
     * fetch = LAZY   → les cours ne sont chargés qu'à la demande
     *
     * @JsonIgnore → coupe la récursion JSON :
     *   CoursClassroom → Classe → [coursClassrooms] → CoursClassroom → ...
     *
     * ══ PAS DE CHAMP utilisateurs ══
     * La relation Utilisateur→Classe est UNIDIRECTIONNELLE.
     * Classe ne doit PAS avoir de List<Utilisateur>.
     * La FK est dans la table "utilisateur", pas ici.
     */
    @OneToMany(mappedBy = "classe", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<CoursClassroom> coursClassrooms;

    // ── Constructeurs ─────────────────────────────────────────────────────

    public Classe() {}

    public Classe(String titre, Niveau niveau) {
        this.titre  = titre;
        this.niveau = niveau;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────

    public Integer             getCodeClasse()                               { return codeClasse; }
    public void                setCodeClasse(Integer codeClasse)             { this.codeClasse = codeClasse; }

    public String              getTitre()                                    { return titre; }
    public void                setTitre(String titre)                        { this.titre = titre; }

    public Niveau              getNiveau()                                   { return niveau; }
    public void                setNiveau(Niveau niveau)                      { this.niveau = niveau; }

    public List<CoursClassroom> getCoursClassrooms()                         { return coursClassrooms; }
    public void                setCoursClassrooms(List<CoursClassroom> list) { this.coursClassrooms = list; }

    @Override
    public String toString() {
        return "Classe{codeClasse=" + codeClasse
                + ", titre='" + titre + "'"
                + ", niveau=" + niveau + "}";
    }
}