#ifndef SYNTACTIC_H
#define SYNTACTIC_H

/**
 * @brief Validate an email address according to specific rules.
 * 
 * Email format: username@domain, where:
 *  - username contains only [a-z0-9] (alphanumeric lowercase characters)
 *  - domain is in the format <lstring>.<rstring>, where:
 *    - lstring contains one or more [a-z] characters (lowercase)
 *    - rstring contains 2 or 3 [a-z] characters (lowercase)
 * 
 * @param email email address to validate.
 * 
 * @return 1 if valid, 0 if invalid.
 */
int validate_email          (const char * email);

/**
 * @brief Validate the duration of a song in the format hh:mm:ss.
 * 
 * Duration format: hh:mm:ss, where:
 * - hh (hours) must be between 00 and 99
 * - mm (minutes) and ss (seconds) must be between 00 and 59
 * - ':' must be present at positions 2 and 5
 * - Must be exactly 8 characters long
 * 
 * @param duracao duration to validate.
 * 
 * @return 1 if valid, 0 if invalid.
 */
int validate_duration       (const char * duracao);

/**
 * @brief Validate a subscription type.
 * 
 * Subscription type must be one of the following:
 * - premium
 * - normal
 * 
 * @param subscricao subscription type to validate.
 * 
 * @return 1 if valid, 0 if invalid.
 */
int validate_subscription   (const char * subscricao);

/**
 * @brief Validate the type of an artist.
 * 
 * Artist type must be individual if the artist has no constituents.
 * 
 * @param type           type of the artist.
 * @param id_constituent id of the constituent artist.
 * 
 * @return 1 if valid, 0 if invalid.
 */
int validate_artist_type    (const char * type, const char * id_constituent);

/**
 * @brief Validate the type of a platform.
 * 
 * Platform type must be one of the following:
 * - mobile
 * - desktop
 * 
 * @param type platform type to validate.
 * 
 * @return 1 if valid, 0 if invalid.
 */
int validate_plataform_type (const char * type);

/**
 * @brief Validate a date in the format yyyy/mm/dd.
 * 
 * Date format: yyyy/mm/dd, where:
 * - Must be exactly 10 characters long
 * - Must contain '/' at positions 4 and 7
 * - Year must be between 0001 and 2024
 * - Month must be between 01 and 12
 * - Day must be between 01 and 31
 * - Date cannot be more recent than 2024/09/09
 * 
 * @param data date to validate.
 * 
 * @return 1 if valid, 0 if invalid.
 */
int validate_date           (const char * data);

#endif // SYNTACTIC_H
