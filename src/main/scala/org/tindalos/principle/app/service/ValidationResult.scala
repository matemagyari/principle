package org.tindalos.principle.app.service

/**
 * Represents the result of a validation operation.
 * Scala case class provides similar immutability and functionality as Java records.
 *
 * @param success true if validation passed, false otherwise
 * @param message error message if validation failed, empty string if successful
 */
case class ValidationResult(success: Boolean, message: String)

object ValidationResult {
  /**
   * Creates a successful validation result with no error message.
   */
  def successful: ValidationResult = ValidationResult(success = true, message = "")

  /**
   * Creates a failed validation result with the given error message.
   */
  def failure(message: String): ValidationResult = ValidationResult(success = false, message = message)
}

