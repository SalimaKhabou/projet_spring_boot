package tn.fst.projet_jee.repositories;


import tn.fst.projet_jee.entities.CoursClassroom;
import tn.fst.projet_jee.entities.Niveau;
import tn.fst.projet_jee.entities.Specialite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository JPA pour l'entité CoursClassroom.
 *
 * Contient des requêtes JPQL pour :
 * - Désaffecter un cours de sa classe
 * - Archiver tous les cours
 * - Calculer le total d'heures par spécialité et niveau
 */
@Repository
public interface CoursClassroomRepository extends JpaRepository<CoursClassroom, Integer> {

    /**
     * Désaffecte un CoursClassroom de sa classe en mettant la FK "classe" à null.
     *
     * @Modifying : indique que cette requête modifie des données (UPDATE/DELETE).
     * @Transactional sera requis au niveau du service.
     *
     * @param idCours l'identifiant du cours à désaffecter
     */
    @Modifying
    @Query("UPDATE CoursClassroom cc SET cc.classe = null WHERE cc.idCours = :idCours")
    void desaffecterDeLaClasse(@Param("idCours") Integer idCours);

    /**
     * Archive tous les cours existants (met archive = true).
     *
     * @Modifying : requête de mise à jour en masse.
     */
    @Modifying
    @Query("UPDATE CoursClassroom cc SET cc.archive = true")
    void archiverTous();

    /**
     * Calcule la somme des heures de cours pour une spécialité et un niveau donnés.
     *
     * Requête JPQL :
     * - Joint CoursClassroom avec sa Classe via cc.classe
     * - Filtre sur la spécialité du cours ET le niveau de la classe
     * - Retourne la somme de nbHeures (SUM)
     *
     * @param sp la spécialité filtrée
     * @param nv le niveau filtré
     * @return la somme des heures, ou null si aucun cours trouvé
     */
    @Query("SELECT SUM(cc.nbHeures) FROM CoursClassroom cc " +
            "JOIN cc.classe c " +
            "WHERE cc.specialite = :sp AND c.niveau = :nv")
    Integer sumNbHeuresBySpecialiteAndNiveau(@Param("sp") Specialite sp, @Param("nv") Niveau nv);

    /**
     * Récupère tous les CoursClassroom (utilisé par le scheduler d'archivage).
     * Méthode héritée de JpaRepository : findAll()
     * (déjà disponible, pas besoin de la redéclarer)
     */
}