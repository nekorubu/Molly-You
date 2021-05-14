package org.thoughtcrime.securesms.emoji

import android.net.Uri
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.thoughtcrime.securesms.components.emoji.CompositeEmojiPageModel
import org.thoughtcrime.securesms.components.emoji.Emoji
import org.thoughtcrime.securesms.components.emoji.EmojiPageModel
import org.thoughtcrime.securesms.components.emoji.StaticEmojiPageModel
import org.thoughtcrime.securesms.util.Hex
import java.io.InputStream
import java.nio.charset.Charset

typealias UriFactory = (sprite: String, format: String) -> Uri

/**
 * Takes an emoji_data.json file data and parses it into an EmojiSource
 */
object EmojiJsonParser {
  private val OBJECT_MAPPER = ObjectMapper()

  @JvmStatic
  fun verify(body: InputStream) {
    parse(body) { _, _ -> Uri.EMPTY }.getOrThrow()
  }

  fun parse(body: InputStream, uriFactory: UriFactory): Result<ParsedEmojiData> {
    return try {
      Result.success(buildEmojiSourceFromNode(OBJECT_MAPPER.readTree(body), uriFactory))
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  private fun buildEmojiSourceFromNode(node: JsonNode, uriFactory: UriFactory): ParsedEmojiData {
    val format: String = node["format"].textValue()
    val obsolete: List<ObsoleteEmoji> = node["obsolete"].toObseleteList()
    val dataPages: List<EmojiPageModel> = getDataPages(format, node["emoji"], uriFactory)
    val displayPages: List<EmojiPageModel> = mergeToDisplayPages(dataPages)
    val metrics: EmojiMetrics = node["metrics"].toEmojiMetrics()
    val densities: List<String> = node["densities"].toDensityList()

    return ParsedEmojiData(metrics, densities, format, displayPages, dataPages, obsolete)
  }

  private fun getDataPages(format: String, emoji: JsonNode, uriFactory: UriFactory): List<EmojiPageModel> {
    return emoji.fields()
      .asSequence()
      .sortedWith { lhs, rhs ->
        val lhsCategory = EmojiCategory.forKey(lhs.key.asCategoryKey())
        val rhsCategory = EmojiCategory.forKey(rhs.key.asCategoryKey())
        val comp = lhsCategory.priority.compareTo(rhsCategory.priority)

        if (comp == 0) {
          val lhsIndex = lhs.key.getPageIndex()
          val rhsIndex = rhs.key.getPageIndex()

          lhsIndex.compareTo(rhsIndex)
        } else {
          comp
        }
      }
      .map { createPage(it.key, format, it.value, uriFactory) }
      .toList()
  }

  private fun createPage(pageName: String, format: String, page: JsonNode, uriFactory: UriFactory): EmojiPageModel {
    val category = EmojiCategory.forKey(pageName.asCategoryKey())
    val pageList = page.mapIndexed { i, data ->
      if (data.size() == 0) {
        throw IllegalStateException("Page index $pageName.$i had no data")
      } else {
        Emoji(data.map { it.textValue().encodeAsUtf16() })
      }
    }

    return StaticEmojiPageModel(category.icon, pageList, uriFactory(pageName, format))
  }

  private fun mergeToDisplayPages(dataPages: List<EmojiPageModel>): List<EmojiPageModel> {
    return dataPages.groupBy { it.iconAttr }
      .map { (icon, pages) -> if (pages.size <= 1) pages.first() else CompositeEmojiPageModel(icon, pages) }
  }
}

private fun JsonNode?.toObseleteList(): List<ObsoleteEmoji> {
  return if (this == null) {
    listOf()
  } else {
    map { node ->
      ObsoleteEmoji(node["obsoleted"].textValue().encodeAsUtf16(), node["replace_with"].textValue().encodeAsUtf16())
    }.toList()
  }
}

private fun JsonNode.toEmojiMetrics(): EmojiMetrics {
  return EmojiMetrics(this["raw_width"].asInt(), this["raw_height"].asInt(), this["per_row"].asInt())
}

private fun JsonNode.toDensityList(): List<String> {
  return map { it.textValue() }
}

private fun String.encodeAsUtf16() = String(Hex.fromStringCondensed(this), Charset.forName("UTF-16"))
private fun String.asCategoryKey() = replace("(_\\d+)*$".toRegex(), "")
private fun String.getPageIndex() = "^.*_(\\d+)+$".toRegex().find(this)?.let { it.groupValues[1] }?.toInt() ?: throw IllegalStateException("No index.")

data class ParsedEmojiData(
  override val metrics: EmojiMetrics,
  override val densities: List<String>,
  override val format: String,
  override val displayPages: List<EmojiPageModel>,
  override val dataPages: List<EmojiPageModel>,
  override val obsolete: List<ObsoleteEmoji>
) : EmojiData
