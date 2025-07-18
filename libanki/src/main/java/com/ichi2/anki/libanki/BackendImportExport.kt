/***************************************************************************************
 * Copyright (c) 2022 Ankitects Pty Ltd <http://apps.ankiweb.net>                       *
 *                                                                                      *
 * This program is free software; you can redistribute it and/or modify it under        *
 * the terms of the GNU General Public License as published by the Free Software        *
 * Foundation; either version 3 of the License, or (at your option) any later           *
 * version.                                                                             *
 *                                                                                      *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY      *
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A      *
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.             *
 *                                                                                      *
 * You should have received a copy of the GNU General Public License along with         *
 * this program.  If not, see <http://www.gnu.org/licenses/>.                           *
 ****************************************************************************************/

package com.ichi2.anki.libanki

import anki.import_export.ExportLimit
import anki.import_export.ImportAnkiPackageOptions
import anki.import_export.ImportResponse
import anki.import_export.exportAnkiPackageOptions
import anki.search.SearchNode
import net.ankiweb.rsdroid.Backend

/**
 * (Maybe) create a colpkg backup, while keeping the collection open. If the
 * configured backup interval has not elapsed, and force=false, no backup will be created,
 * and this routine will return false.
 *
 * There must not be an active transaction.
 *
 * If `waitForCompletion` is true, block until the backup completes. Otherwise this routine
 * returns quickly, and the backup can be awaited on a background thread with awaitBackupCompletion()
 * to check for success.
 *
 * Backups are automatically expired according to the user's settings.
 *
 */
fun Collection.createBackup(
    backupFolder: String,
    force: Boolean,
    waitForCompletion: Boolean,
): Boolean =
    backend.createBackup(
        backupFolder = backupFolder,
        force = force,
        waitForCompletion = waitForCompletion,
    )

/**
 * If a backup is running, block until it completes, throwing if it fails, or already
 * failed, and the status has not yet been checked. On failure, an error is only returned
 * once; subsequent calls are a no-op until another backup is run.
 */
fun Collection.awaitBackupCompletion() {
    backend.awaitBackupCompletion()
}

/**
 * Replace the collection file with the one in the provided .colpkg file.
 * The collection must be already closed, and must be opened afterwards.
 * */
fun importCollectionPackage(
    backend: Backend,
    colPath: CollectionFiles,
    colpkgPath: String,
) {
    backend.importCollectionPackage(
        colPath = colPath.colDb.absolutePath,
        backupPath = colpkgPath,
        mediaFolder = colPath.mediaFolder.absolutePath,
        mediaDb = colPath.mediaDb.absolutePath,
    )
}

fun Collection.importAnkiPackage(
    packagePath: String,
    options: ImportAnkiPackageOptions,
): ImportResponse = backend.importAnkiPackage(packagePath, options)

fun Collection.importAnkiPackageRaw(input: ByteArray): ByteArray = backend.importAnkiPackageRaw(input)

fun Collection.getImportAnkiPackagePresetsRaw(input: ByteArray): ByteArray = backend.getImportAnkiPackagePresetsRaw(input)

/**
 * Export the specified deck to an .apkg file.
 * * If legacy is false, an apkg will be created that can only
 * be opened with recent Anki versions.
 */
fun Collection.exportAnkiPackage(
    outPath: String,
    withScheduling: Boolean,
    withDeckConfigs: Boolean,
    withMedia: Boolean,
    limit: ExportLimit,
    legacy: Boolean,
) {
    val options =
        exportAnkiPackageOptions {
            this.withScheduling = withScheduling
            this.withMedia = withMedia
            this.legacy = legacy
            this.withDeckConfigs = withDeckConfigs
        }
    backend.exportAnkiPackage(outPath, options, limit)
}

fun Collection.exportNotesCsv(
    outPath: String,
    withHtml: Boolean,
    withTags: Boolean,
    withDeck: Boolean,
    withNotetype: Boolean,
    withGuid: Boolean,
    limit: ExportLimit,
) {
    backend.exportNoteCsv(outPath, withHtml, withTags, withDeck, withNotetype, withGuid, limit)
}

fun Collection.exportCardsCsv(
    outPath: String,
    withHtml: Boolean,
    limit: ExportLimit,
) {
    backend.exportCardCsv(outPath, withHtml, limit)
}

fun Collection.getCsvMetadataRaw(input: ByteArray): ByteArray = backend.getCsvMetadataRaw(input)

fun Collection.importCsvRaw(input: ByteArray): ByteArray = backend.importCsvRaw(input)

fun Collection.buildSearchString(input: ByteArray): String = backend.buildSearchString(SearchNode.parseFrom(input))
