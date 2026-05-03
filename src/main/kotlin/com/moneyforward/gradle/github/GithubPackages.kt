package com.moneyforward.gradle.github

import java.net.URI

const val GPR_URL = "https://maven.pkg.github.com/"

/**
 * Returns a new [URI] by prepending the github-packages url to the given string
 * @param ownerAndRepository the owner/repository for GPR (example: evanc-mfw/private-repository-plugin)
 * @return the built GPR URI (example: https://maven.pkg.github.com/evanc-mfw/private-repository-plugin)
 */
fun gpr(ownerAndRepository: String): URI {
    return URI(GPR_URL + ownerAndRepository.trim('/'))
}

/**
 * Returns a new [URI] by prepending the github-packages url to the given owner and repository
 * @param owner the owner of the target repository (example: evanc-mfw)
 * @param repository the repository under the owner's account (example: private-repository-plugin)
 * @return the built GPR URI (example: https://maven.pkg.github.com/evanc-mfw/private-repository-plugin)
 */
fun gpr(
    owner: String,
    repository: String,
): URI {
    return URI(GPR_URL + owner.trim('/') + '/' + repository.trim('/'))
}
