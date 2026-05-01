package tn.fst.projet_jee.services;



import tn.fst.projet_jee.entities.*;

/**
 * Interface définissant les contrats (signatures) des services métier.
 *
 * Bonne pratique Spring : on programme toujours sur l'interface,
 * jamais directement sur l'implémentation. Cela facilite :
 * - Le remplacement de l'implémentation sans changer le contrôleur
 * - Les tests unitaires (mocking facile)
 */
public interface ICoursClassroomService {

    /**
     * Ajoute un nouvel utilisateur en base de données.
     * @param utilisateur l'objet Utilisateur à persister
     * @return l'utilisateur sauvegardé (avec son ID généré)
     */
    Utilisateur ajouterUtilisateur(Utilisateur utilisateur);

    /**
     * Ajoute une nouvelle classe en base de données.
     * @param c l'objet Classe à persister
     * @return la classe sauvegardée (avec son codeClasse généré)
     */
    Classe ajouterClasse(Classe c);

    /**
     * Ajoute un CoursClassroom et l'affecte à une classe existante.
     * @param cc          le cours à persister
     * @param codeClasse  l'identifiant de la classe à laquelle affecter le cours
     * @return le CoursClassroom sauvegardé avec sa classe associée
     */
    CoursClassroom ajouterCoursClassroom(CoursClassroom cc, Integer codeClasse);

    /**
     * Affecte un utilisateur existant à une classe existante (relation ManyToMany).
     * @param idUtilisateur l'identifiant de l'utilisateur
     * @param codeClasse    l'identifiant de la classe
     */
    void affecterUtilisateurClasse(Integer idUtilisateur, Integer codeClasse);

    /**
     * Compte le nombre d'utilisateurs appartenant aux classes d'un niveau donné.
     * @param nv le niveau à filtrer
     * @return le nombre d'utilisateurs
     */
    Integer nbUtilisateursParNiveau(Niveau nv);

    /**
     * Désaffecte un CoursClassroom de sa classe (met la FK classe à null).
     * @param idCours l'identifiant du cours à désaffecter
     */
    void desaffecterCoursClassroomClasse(Integer idCours);

    /**
     * Archive tous les CoursClassroom existants (archive = true).
     * Déclenchée toutes les 60 secondes par le Spring Scheduler.
     */
    void archiverCoursClassrooms();

    /**
     * Calcule le total des heures enseignées pour une spécialité et un niveau donnés.
     * @param sp la spécialité filtrée
     * @param nv le niveau filtré
     * @return le nombre total d'heures
     */
    Integer nbHeuresParSpecEtNiv(Specialite sp, Niveau nv);
}