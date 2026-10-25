// file: app/src/main/java/ru/beyedono/app/ui/screen/policy/PrivacyPolicyScreen.kt
package ru.beyedono.app.ui.screen.policy

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Политика конфиденциальности") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Политика в отношении обработки персональных данных",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "1. Общие положения\n" +
                        "Настоящая политика обработки персональных данных составлена в соответствии с требованиями Федерального закона от 27.07.2006. №152-ФЗ «О персональных данных» и определяет порядок обработки персональных данных и меры по обеспечению безопасности персональных данных, предпринимаемые кафе «Бэйэ Дьоно» (далее — Оператор).\n\n" +
                        "2. Основные понятия\n" +
                        "Персональные данные — любая информация, относящаяся к прямо или косвенно определенному или определяемому физическому лицу (субъекту персональных данных).\n" +
                        "Обработка персональных данных — любое действие (операция) или совокупность действий (операций), совершаемых с использованием средств автоматизации или без использования таких средств с персональными данными.\n\n" +
                        "3. Категории обрабатываемых данных\n" +
                        "Оператор обрабатывает следующие персональные данные Пользователя:\n" +
                        "• Имя;\n" +
                        "• Номер мобильного телефона;\n" +
                        "• Адрес электронной почты;\n" +
                        "• Адрес доставки;\n" +
                        "• История заказов.\n\n" +
                        "4. Цели обработки персональных данных\n" +
                        "• Оформление и доставка заказов;\n" +
                        "• Информирование Пользователя посредством отправки push-уведомлений о статусе заказа;\n" +
                        "• Улучшение качества обслуживания.\n\n" +
                        "5. Правовые основания обработки\n" +
                        "• Федеральный закон от 27.07.2006 №152-ФЗ «О персональных данных»;\n" +
                        "• Согласие Пользователя на обработку персональных данных.\n\n" +
                        "6. Порядок и условия обработки\n" +
                        "Обработка персональных данных осуществляется с согласия Пользователя. Базы данных, содержащие персональные данные граждан Российской Федерации, находятся на серверах, расположенных на территории Российской Федерации.\n\n" +
                        "7. Контактные данные Оператора\n" +
                        "Телефон: 8 914 102 29 08\n" +
                        "Село Бердигестях, ул. Коврова 18 А",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}