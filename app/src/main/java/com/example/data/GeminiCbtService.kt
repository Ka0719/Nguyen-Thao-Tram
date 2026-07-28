package com.example.data

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiCbtService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

    suspend fun reframePerfectionistThought(negativeThought: String): String = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Exception) { "" }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getFallbackReframing(negativeThought)
        }

        val prompt = """
            Bạn là một chuyên viên tâm lý học đường ấm áp, tinh tế, am hiểu Liệu pháp Nhận thức Hành vi (CBT) và Liệu pháp Chấp nhận & Cam kết (ACT).
            Học sinh trung học đang gặp suy nghĩ cầu toàn / áp lực đồng trang lứa sau:
            "$negativeThought"
            
            Hãy giúp học sinh tái cấu trúc nhận thức (Cognitive Reframing) theo cách ngắn gọn (3-4 câu), ấm áp, thực tế, giúp các em nhẹ lòng:
            1. Phản biện nhẹ nhàng cạm bẫy tư duy (như tư duy Trắng-Đen, tự gán nhãn).
            2. Đưa ra góc nhìn thực tế, bao dung với bản thân ("Good enough").
            3. Một lời động viên ngắn mang năng lượng bình thản, chữa lành.
            Việt Nam, xưng "Mình" hoặc "Chuyên viên" và gọi học sinh là "bạn" hoặc "cầu thủ nhỏ".
        """.trimIndent()

        try {
            val jsonPayload = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        })
                    })
                })
            }

            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(jsonPayload.toString().toRequestBody("application/json".toMediaType()))
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext getFallbackReframing(negativeThought)
                }
                val responseBody = response.body?.string() ?: ""
                val responseJson = JSONObject(responseBody)
                val text = responseJson
                    .optJSONArray("candidates")
                    ?.optJSONObject(0)
                    ?.optJSONObject("content")
                    ?.optJSONArray("parts")
                    ?.optJSONObject(0)
                    ?.optString("text")

                if (!text.isNullOrBlank()) {
                    text.trim()
                } else {
                    getFallbackReframing(negativeThought)
                }
            }
        } catch (e: Exception) {
            getFallbackReframing(negativeThought)
        }
    }

    suspend fun analyzeTriggerPattern(contextStr: String, emotionStr: String, behaviorStr: String): String = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Exception) { "" }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getFallbackTriggerAnalysis(contextStr, emotionStr, behaviorStr)
        }

        val prompt = """
            Phân tích mô thức tâm lý kích hoạt (Trigger Pattern) cho học sinh:
            - Sự kiện kích hoạt (Kéo MXH): $contextStr
            - Cảm xúc phát sinh: $emotionStr
            - Hành vi bộc phát (Năng suất độc hại): $behaviorStr

            Hãy đưa ra phân tích ngắn (3 câu):
            1. Chỉ ra mô thức độc hại (VD: So sánh xã hội -> Sợ tụt hậu -> Hành vi trừng phạt bản thân).
            2. Nhắc nhở lòng trắc ẩn.
            3. Hành động nhỏ để xoa dịu ngay (VD: đóng app, hít thở 3 nhịp, đi uống 1 ly nước ấm).
        """.trimIndent()

        try {
            val jsonPayload = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        })
                    })
                })
            }

            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(jsonPayload.toString().toRequestBody("application/json".toMediaType()))
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext getFallbackTriggerAnalysis(contextStr, emotionStr, behaviorStr)
                }
                val responseBody = response.body?.string() ?: ""
                val responseJson = JSONObject(responseBody)
                val text = responseJson
                    .optJSONArray("candidates")
                    ?.optJSONObject(0)
                    ?.optJSONObject("content")
                    ?.optJSONArray("parts")
                    ?.optJSONObject(0)
                    ?.optString("text")

                if (!text.isNullOrBlank()) {
                    text.trim()
                } else {
                    getFallbackTriggerAnalysis(contextStr, emotionStr, behaviorStr)
                }
            }
        } catch (e: Exception) {
            getFallbackTriggerAnalysis(contextStr, emotionStr, behaviorStr)
        }
    }

    private fun getFallbackReframing(thought: String): String {
        return when {
            thought.contains("thất bại", ignoreCase = true) || thought.contains("kém", ignoreCase = true) ->
                "💡 **Phân tích CBT**: Bạn đang rơi vào bẫy tư duy 'Trắng - Đen' (Cho rằng nếu không hoàn hảo nghĩa là thất bại hoàn toàn).\n\n🌱 **Góc nhìn thực tế**: Giá trị bản thân bạn không nằm ở một con điểm hay lời khen trên mạng. Mỗi bước đi là một sự cố gắng xứng đáng ghi nhận.\n\n✨ **Nhắc nhở**: Hom nay bạn đã làm rất tốt rồi. Hãy buông bớt tiêu chuẩn khắt khe và nghỉ ngơi nhé!"
            thought.contains("so sánh", ignoreCase = true) || thought.contains("IELTS", ignoreCase = true) || thought.contains("bạn", ignoreCase = true) ->
                "💡 **Phân tích CBT**: MXH chỉ khoe những khoảnh khắc rực rỡ nhất (Highlight Reel), không phản ánh toàn bộ cuộc sống thực.\n\n🌱 **Góc nhìn thực tế**: Hành trình của mỗi người có nhịp độ riêng. So sánh xuất phát điểm của mình với kết quả của người khác chỉ làm bạn mệt mỏi.\n\n✨ **Nhắc nhở**: Tập trung vào từng tiến bộ nho nhỏ của chính mình thôi nhé!"
            else ->
                "💡 **Phân tích CBT**: Suy nghĩ này đang gây áp lực không cần thiết lên bạn. Đó chỉ là một tư duy bộc phát, không phải sự thật tuyệt đối.\n\n🌱 **Góc nhìn thực tế**: Cầu toàn thích nghi là cố gắng hết sức và chấp nhận kết quả 'Đủ tốt', không phải vắt kiệt sức lực đến kiệt sức.\n\n✨ **Nhắc nhở**: Bạn hoàn toàn 'Đủ' ngay lúc này!"
        }
    }

    private fun getFallbackTriggerAnalysis(contextStr: String, emotionStr: String, behaviorStr: String): String {
        return "⚠️ **Mô thức độc hại**: Kích hoạt từ '$contextStr' dẫn đến cảm xúc '$emotionStr' và phản ứng ép bản thân '$behaviorStr'. Đây là vòng lặp so sánh xã hội gây kiệt sức (Burnout Cycle).\n\n🌿 **Xoa dịu ngay**: Hãy úp điện thoại xuống, hít vào thật sâu trong 4 giây và thở ra chậm trong 6 giây. Bạn không cần phải trừng phạt cơ thể mình vì những gì thấy trên màn hình."
    }
}
