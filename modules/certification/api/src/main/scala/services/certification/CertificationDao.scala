package services.certification

import models.certification.Certification

trait CertificationDao {

  /**
   * Someone has registered interest in certification
   */
  def registerInterest(certification: Certification): Unit

  /**
   * Find all people that have registered interest in certification
   */
  def findAllInterested(): Seq[Certification]
}
