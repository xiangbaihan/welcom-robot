/*
 * 语音听写(iFly Auto Transform)技术能够实时地将语音转换成对应的文字。
 */
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <errno.h>
#include <unistd.h>

#include <jni.h>

#include "../../include/qisr.h"
#include "../../include/msp_cmn.h"
#include "../../include/msp_errors.h"
#include "speech_recognizer.h"
#include "com_nb_robot_xf_AsrNative.h"

#define FRAME_LEN	640 
#define	BUFFER_SIZE	4096
#define SAMPLE_RATE_16K     (16000)
#define SAMPLE_RATE_8K      (8000)
#define MAX_GRAMMARID_LEN   (32)
#define MAX_PARAMS_LEN      (1024)

const char * ASR_RES_PATH = "fo|res/asr/common.jet"; //离线语法识别资源路径
const char * GRM_BUILD_PATH = "res/asr/GrmBuilld"; //构建离线语法识别网络生成数据保存路径
// const char * GRM_FILE = "call.bnf"; //构建离线识别语法网络所用的语法文件
// const char * LEX_NAME = "contact"; //更新离线识别语法的contact槽（语法文件为此示例中使用的call.bnf）
const char * GRM_FILE = "bank.bnf"; //构建离线识别语法网络所用的语法文件
const char * LEX_NAME = "robotName"; //更新离线识别语法的contact槽（语法文件为此示例中使用的bank.bnf）

typedef struct _UserData {
	int build_fini; //标识语法构建是否完成
	int update_fini; //标识更新词典是否完成
	int errcode; //记录语法构建或更新词典回调错误码
	char grammar_id[MAX_GRAMMARID_LEN]; //保存语法构建返回的语法ID
} UserData;

const char *get_audio_file(void); //选择进行离线语法识别的语音文件
int build_grammar(const char *grm_file_path, UserData *udata); //构建离线识别语法网络
int update_lexicon(const char *lex_name, const char *lex_content, UserData *udata); //更新离线识别语法词典
int run_asr(UserData *udata); //进行离线语法识别

const char* get_audio_file(void) {
	int key = 1;
	printf("\n1.打电话给丁伟\n");
	return "wav/ddhgdw.pcm";

	while (key != 27) //按Esc则退出
	{
		printf("请选择音频文件：\n");
		printf("1.打电话给丁伟\n");
		printf("2.打电话给黄辣椒\n");
		//scanf("%d", &key);
		//key = getc();
		//printf("key==========%c",key);
		switch (key) {
		case 1:
			printf("\n1.打电话给丁伟\n");
			return "wav/ddhgdw.pcm";
		case 2:
			printf("\n2.打电话给黄辣椒\n");
			return "wav/ddhghlj.pcm";
		default:
			continue;
		}
	}
	exit(0);
	return NULL;
}

int build_grm_cb(int ecode, const char *info, void *udata) {
	UserData *grm_data = (UserData *) udata;

	if (NULL != grm_data) {
		grm_data->build_fini = 1;
		grm_data->errcode = ecode;
	}

	if (MSP_SUCCESS == ecode && NULL != info) {
		printf("构建语法成功！ 语法ID:%s\n", info);
		if (NULL != grm_data)
			snprintf(grm_data->grammar_id, MAX_GRAMMARID_LEN - 1, info);
	} else
		printf("构建语法失败！%d\n", ecode);

	return 0;
}

int build_grammar(const char *grm_file_path, UserData *udata) {
	FILE *grm_file = NULL;
	char *grm_content = NULL;
	unsigned int grm_cnt_len = 0;
	char grm_build_params[MAX_PARAMS_LEN] = { NULL };
	int ret = 0;

	grm_file = fopen(grm_file_path, "rb");
	if (NULL == grm_file) {
		printf("打开\"%s\"文件失败！[%s]\n", grm_file_path, strerror(errno));
		return -1;
	}

	fseek(grm_file, 0, SEEK_END);
	grm_cnt_len = ftell(grm_file);
	fseek(grm_file, 0, SEEK_SET);

	grm_content = (char *) malloc(grm_cnt_len + 1);
	if (NULL == grm_content) {
		printf("内存分配失败!\n");
		fclose(grm_file);
		grm_file = NULL;
		return -1;
	}
	fread((void*) grm_content, 1, grm_cnt_len, grm_file);
	grm_content[grm_cnt_len] = '\0';
	fclose(grm_file);
	grm_file = NULL;

	snprintf(grm_build_params, MAX_PARAMS_LEN - 1,
			"engine_type = local, \
		asr_res_path = %s, sample_rate = %d, \
		grm_build_path = %s, ",
			ASR_RES_PATH,
			SAMPLE_RATE_16K, GRM_BUILD_PATH);
	ret = QISRBuildGrammar("bnf", grm_content, grm_cnt_len, grm_build_params,
			build_grm_cb, udata);

	free(grm_content);
	grm_content = NULL;

	return ret;
}

int update_lex_cb(int ecode, const char *info, void *udata) {
	UserData *lex_data = (UserData *) udata;

	if (NULL != lex_data) {
		lex_data->update_fini = 1;
		lex_data->errcode = ecode;
	}

	if (MSP_SUCCESS == ecode)
		printf("更新词典成功！\n");
	else
		printf("更新词典失败！%d\n", ecode);

	return 0;
}

int update_lexicon(const char *lex_name, const char *lex_content, UserData *udata) {
	unsigned int lex_cnt_len = strlen(lex_content);
	char update_lex_params[MAX_PARAMS_LEN] = { NULL };
	//printf("lex_name: %s\n", lex_name);
	//printf("lex_content: %s\n", lex_content);

	snprintf(update_lex_params, MAX_PARAMS_LEN - 1,
			"engine_type = local, text_encoding = UTF-8, \
		asr_res_path = %s, sample_rate = %d, \
		grm_build_path = %s, grammar_list = %s, ",
			ASR_RES_PATH,
			SAMPLE_RATE_16K, GRM_BUILD_PATH, udata->grammar_id);
	return QISRUpdateLexicon(lex_name, lex_content, lex_cnt_len,
			update_lex_params, update_lex_cb, udata);
}

static void show_result(char *string, char is_over) {
	//printf("\rResult: [ %s ]", string);
	if (is_over)
		putchar('\n');
}

static char *g_result = NULL;
static unsigned int g_buffersize = BUFFER_SIZE;
// 0 means no, 1 means yes.
static int g_wait_for_result = 0;

void on_result(const char *result, char is_last) {
	if (result) {
		size_t left = g_buffersize - 1 - strlen(g_result);
		size_t size = strlen(result);
		if (left < size) {
			g_result = (char*) realloc(g_result, g_buffersize + BUFFER_SIZE);
			if (g_result)
				g_buffersize += BUFFER_SIZE;
			else {
				printf("mem alloc failed\n");
				return;
			}
		}
		strncat(g_result, result, size);
		show_result(g_result, is_last);
		g_wait_for_result = 0;
	}
}
void on_speech_begin() {
	if (g_result) {
		free(g_result);
	}
	g_result = (char*) malloc(BUFFER_SIZE);
	g_buffersize = BUFFER_SIZE;
	memset(g_result, 0, g_buffersize);
	g_wait_for_result = 1;
	printf("Start Listening...\n");
}
void on_speech_end(int reason) {
	if (reason == END_REASON_VAD_DETECT)
		printf("\nSpeaking done \n");
	else
		printf("\nRecognizer error %d\n", reason);
	g_wait_for_result = 0;
}

/* demo send audio data from a file */
static void demo_file(const char* audio_file, const char* session_begin_params) {
	int errcode = 0;
	FILE* f_pcm = NULL;
	char* p_pcm = NULL;
	unsigned long pcm_count = 0;
	unsigned long pcm_size = 0;
	unsigned long read_size = 0;
	struct speech_rec iat;
	struct speech_rec_notifier recnotifier = { on_result, on_speech_begin,
			on_speech_end };

	if (NULL == audio_file)
		goto iat_exit;

	f_pcm = fopen(audio_file, "rb");
	if (NULL == f_pcm) {
		printf("\nopen [%s] failed! \n", audio_file);
		goto iat_exit;
	}

	fseek(f_pcm, 0, SEEK_END);
	pcm_size = ftell(f_pcm);
	fseek(f_pcm, 0, SEEK_SET);

	p_pcm = (char *) malloc(pcm_size);
	if (NULL == p_pcm) {
		printf("\nout of memory! \n");
		goto iat_exit;
	}

	read_size = fread((void *) p_pcm, 1, pcm_size, f_pcm);
	if (read_size != pcm_size) {
		printf("\nread [%s] error!\n", audio_file);
		goto iat_exit;
	}

	errcode = sr_init(&iat, session_begin_params, SR_USER, &recnotifier);
	if (errcode) {
		printf("speech recognizer init failed : %d\n", errcode);
		goto iat_exit;
	}

	errcode = sr_start_listening(&iat);
	if (errcode) {
		printf("\nsr_start_listening failed! error code:%d\n", errcode);
		goto iat_exit;
	}

	while (1) {
		unsigned int len = 10 * FRAME_LEN; /* 200ms audio */
		int ret = 0;

		if (pcm_size < 2 * len)
			len = pcm_size;
		if (len <= 0)
			break;

		ret = sr_write_audio_data(&iat, &p_pcm[pcm_count], len);

		if (0 != ret) {
			printf("\nwrite audio data failed! error code:%d\n", ret);
			goto iat_exit;
		}

		pcm_count += (long) len;
		pcm_size -= (long) len;
	}

	errcode = sr_stop_listening(&iat);
	if (errcode) {
		printf("\nsr_stop_listening failed! error code:%d \n", errcode);
		goto iat_exit;
	}

	iat_exit: if (NULL != f_pcm) {
		fclose(f_pcm);
		f_pcm = NULL;
	}
	if (NULL != p_pcm) {
		free(p_pcm);
		p_pcm = NULL;
	}

	sr_stop_listening(&iat);
	sr_uninit(&iat);
}

/* demo recognize the audio from microphone */
static void demo_mic(const char* session_begin_params) {
	int errcode;
	struct speech_rec iat;
	struct speech_rec_notifier recnotifier = { on_result, on_speech_begin,
			on_speech_end };

	errcode = sr_init(&iat, session_begin_params, SR_MIC, &recnotifier);
	if (errcode) {
		printf("speech recognizer init failed\n");
		return;
	}
	errcode = sr_start_listening(&iat);
	if (errcode) {
		printf("start listen failed %d\n", errcode);
	}

	int max_listen_seconds = 10;
	int second = 0;
	while (second < max_listen_seconds && g_wait_for_result == 1) {
		sleep(1);
		second++;
	}
	errcode = sr_stop_listening(&iat);
	if (errcode) {
		printf("stop listening failed %d\n", errcode);
	}

	sr_uninit(&iat);
}

int run_asr(UserData *udata) {
	char asr_params[MAX_PARAMS_LEN] = { NULL };
	//const char *rec_rslt = NULL;
	//const char *session_id = NULL;
	const char *asr_audiof = NULL;
	//FILE *f_pcm = NULL;
	//char *pcm_data = NULL;
	//long pcm_count = 0;
	//long pcm_size = 0;
	//int last_audio = 0;

	//int aud_stat = MSP_AUDIO_SAMPLE_CONTINUE;
	//int ep_status = MSP_EP_LOOKING_FOR_SPEECH;
	//int rec_status = MSP_REC_STATUS_INCOMPLETE;
	//int rss_status = MSP_REC_STATUS_INCOMPLETE;
	//int errcode = -1;
	int aud_src = 1;
	//离线语法识别参数设置
	snprintf(asr_params, MAX_PARAMS_LEN - 1,
			"engine_type = local, accent = mandarin, \
		asr_res_path = %s, sample_rate = %d, \
		grm_build_path = %s, local_grammar = %s, \
		result_type = json, result_encoding = UTF-8, ",
			ASR_RES_PATH,
			SAMPLE_RATE_16K, GRM_BUILD_PATH, udata->grammar_id);
	//printf("音频数据在哪? \n0: 从文件读入\n1:从MIC说话\n");
	//printf("%d\n", aud_src);
	//scanf("%d", &aud_src);
	if (aud_src != 0) {
		demo_mic(asr_params);
	} else {
		asr_audiof = get_audio_file();
		demo_file(asr_audiof, asr_params);
	}
	return 0;
}

int login() {
	// const char *login_config = "appid = 59213cc1"; //登录参数
	// const char *login_config = "appid = 5957d943"; //登录参数
	//const char *login_config = "appid = 5987d534"; //登录参数
	// const char *login_config = "appid = 59b9e151"; //登录参数
	// const char *login_config = "appid = 59e93e55"; //登录参数
	const char *login_config = "appid = 5a24afba"; //登录参数
	return MSPLogin(NULL, NULL, login_config); //第一个参数为用户名，第二个参数为密码，传NULL即可，第三个参数是登录参数
}

int buildGrammar(const char *grm_file_path, UserData *udata) {
	printf("构建离线识别语法网络...\n");
	int ret = build_grammar(grm_file_path, udata); //第一次使用某语法进行识别，需要先构建语法网络，获取语法ID，之后使用此语法进行识别，无需再次构建
	if (MSP_SUCCESS != ret) {
		return ret;
	}
	while (1 != udata->build_fini)
		usleep(300 * 1000);
	if (MSP_SUCCESS != udata->errcode)
		return udata->errcode;
	return MSP_SUCCESS;
}

int updateLexicon(const char *lex_name, const char *lex_content, UserData *udata) {
	//printf("更新离线语法词典...\n");
	int ret = update_lexicon(lex_name, lex_content, udata); //当语法词典槽中的词条需要更新时，调用QISRUpdateLexicon接口完成更新
	if (MSP_SUCCESS != ret) {
		return ret;
	}
	while (1 != udata->update_fini)
		usleep(300 * 1000);
	if (MSP_SUCCESS != udata->errcode)
		return udata->errcode;
	return MSP_SUCCESS;
}

int logout() {
	printf("退出...\n");
	MSPLogout();
	return 0;
}

int run() {
	int ret = login();
	if (MSP_SUCCESS != ret) {
		printf("登录失败：%d\n", ret);
		return logout();
	}

	UserData asr_data;
	memset(&asr_data, 0, sizeof(UserData));
	ret = buildGrammar(GRM_FILE, &asr_data);
	if (MSP_SUCCESS != ret) {
		printf("构建语法调用失败！\n");
		logout();
	}
	printf("离线识别语法网络构建完成\n");
	// const char *lex_content = "黄辣椒\n老板";
	const char *lex_content = "大黑";
	ret = updateLexicon(LEX_NAME, lex_content, &asr_data);
	if (MSP_SUCCESS != ret) {
		printf("更新词典调用失败！\n");
		return logout();
	}
	printf("更新离线语法词典完成，开始识别...\n");
	ret = run_asr(&asr_data);
	if (MSP_SUCCESS != ret) {
		printf("离线语法识别出错: %d \n", ret);
		return logout();
	}
	return 0;

}

int main(int argc, char* argv[]) {
	return run();
}

/***** JNI functions ***/

// Copy UserData in C to Java UserData.
jobject copyUserDataToJavaObject(JNIEnv* env, const UserData *asr_data)
{
	jstring jstrResult;
	if (g_result) {
		jstrResult = (*env)->NewStringUTF(env, g_result);
	}
	jclass userDataClass = (*env)->FindClass(env, "com/nb/robot/xf/UserData");
	jmethodID constructorMethod = (*env)->GetMethodID(env, userDataClass,
			"<init>", "(IIILjava/lang/String;Ljava/lang/String;)V");
	jobject userDataObject = (*env)->NewObject(env, userDataClass,
			constructorMethod, asr_data->build_fini, asr_data->update_fini,
			asr_data->errcode, (*env)->NewStringUTF(env, asr_data->grammar_id),
			jstrResult);
	return userDataObject;
}

// Fill UserData in C from Java UserData.
void copyUserDataToC(JNIEnv* env, jobject udata, UserData *asr_data)
{
	jclass userDataClass = (*env)->FindClass(env, "com/nb/robot/xf/UserData");

	jmethodID getGrammarId_method = (*env)->GetMethodID(env, userDataClass,
			"getGrammarId", "()Ljava/lang/String;");
	jstring grammar_str = (*env)->CallObjectMethod(env, udata, getGrammarId_method);
	const char* native_grammar_str = (*env)->GetStringUTFChars(env, grammar_str,
			JNI_FALSE);
	strncpy(asr_data->grammar_id, native_grammar_str, MAX_GRAMMARID_LEN);
	(*env)->ReleaseStringUTFChars(env, grammar_str, native_grammar_str);

	jmethodID getBuildFini_method = (*env)->GetMethodID(env, userDataClass,
			"getBuildFini", "()I");
	jint build_fini = (*env)->CallObjectMethod(env, udata, getBuildFini_method);
	asr_data->build_fini = (int)build_fini;

	jmethodID getUpdateFini_method = (*env)->GetMethodID(env, userDataClass,
			"getUpdateFini", "()I");
	jint update_fini = (*env)->CallObjectMethod(env, udata, getUpdateFini_method);
	asr_data->update_fini = (int)update_fini;

	jmethodID getErrorCode_method = (*env)->GetMethodID(env, userDataClass,
			"getErrorCode", "()I");
	jint errorcode = (*env)->CallObjectMethod(env, udata, getErrorCode_method);
	asr_data->errcode = (int)errorcode;
}

/*
 * Class:     com_nb_robot_xf_AsrNative
 * Method:    hello
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_nb_robot_xf_AsrNative_hello
(JNIEnv * env, jobject obj) {
	printf("Hello from C...");
}

/*
 * Class:     com_nb_robot_xf_AsrNative
 * Method:    login
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_nb_robot_xf_AsrNative_login(JNIEnv * env,
		jobject obj) {
	return login();
}

/*
 * Class:     com_nb_robot_xf_AsrNative
 * Method:    logout
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_nb_robot_xf_AsrNative_logout(JNIEnv * env,
		jobject obj) {
	return logout();
}

/*
 * Class:     com_nb_robot_xf_AsrNative
 * Method:    buildGrammar
 * Signature: (Ljava/lang/String;)Lcom/nb/robot/xf/UserData;
 */
JNIEXPORT jobject JNICALL Java_com_nb_robot_xf_AsrNative_buildGrammar(
		JNIEnv *env, jobject obj, jstring bnfFile) {
	const char *bnf_file = (*env)->GetStringUTFChars(env, bnfFile, JNI_FALSE);
	UserData asr_data;
	memset(&asr_data, 0, sizeof(UserData));
	int ret = buildGrammar(bnf_file, &asr_data);
	if (asr_data.errcode == MSP_SUCCESS && ret != MSP_SUCCESS) {
		asr_data.errcode = ret;
	}
	(*env)->ReleaseStringUTFChars(env, bnfFile, bnf_file);
	return copyUserDataToJavaObject(env, &asr_data);
}

/*
 * Class:     com_nb_robot_xf_AsrNative
 * Method:    updateLexicon
 * Signature: (Lcom/nb/robot/xf/UserData;Ljava/lang/String;Ljava/lang/String;)Lcom/nb/robot/xf/UserData;
 */
JNIEXPORT jobject JNICALL Java_com_nb_robot_xf_AsrNative_updateLexicon(
		JNIEnv * env, jobject obj, jobject udata, jstring slotName, jstring keywords) {
	UserData asr_data;
	memset(&asr_data, 0, sizeof(UserData));
	copyUserDataToC(env, udata, &asr_data);

	const char *lex_name = (*env)->GetStringUTFChars(env, slotName, JNI_FALSE);
	const char *lex_content = (*env)->GetStringUTFChars(env, keywords, JNI_FALSE);

	int ret = updateLexicon(lex_name, lex_content, &asr_data);
	if (asr_data.errcode == MSP_SUCCESS && ret != MSP_SUCCESS) {
		asr_data.errcode = ret;
	}
	(*env)->ReleaseStringUTFChars(env, slotName, lex_name);
	(*env)->ReleaseStringUTFChars(env, keywords, lex_content);
	return copyUserDataToJavaObject(env, &asr_data);
}

/*
 * Class:     com_nb_robot_xf_AsrNative
 * Method:    runAsr
 * Signature: (Lcom/nb/robot/xf/UserData;)Lcom/nb/robot/xf/UserData;
 */
JNIEXPORT jobject JNICALL Java_com_nb_robot_xf_AsrNative_runAsr
  (JNIEnv *env, jobject obj, jobject udata) {
	UserData asr_data;
	memset(&asr_data, 0, sizeof(UserData));
	copyUserDataToC(env, udata, &asr_data);

	int ret = run_asr(&asr_data);
	if (asr_data.errcode == MSP_SUCCESS && ret != MSP_SUCCESS) {
		asr_data.errcode = ret;
	}
	return copyUserDataToJavaObject(env, &asr_data);
}
