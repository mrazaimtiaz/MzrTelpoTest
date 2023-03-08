package com.example.mzrtelpotest.presentation

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.example.mzrtelpotest.common.Constants
import com.example.mzrtelpotest.domain.model.SelectDepartment
import com.example.mzrtelpotest.domain.model.SelectService
import kotlinx.coroutines.delay
import java.util.*

import androidx.compose.ui.text.googlefonts.GoogleFont
import com.example.mzrtelpotest.R
import com.example.mzrtelpotest.ui.theme.primarySidra
import com.example.mzrtelpotest.utils.MrzParser
import com.gicproject.onepagequeuekioskapp.Screen
import com.gicproject.onepagequeuekioskapp.presentation.MyEvent
import com.gicproject.onepagequeuekioskapp.presentation.MyViewModel


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SelectServiceScreen(
    selectDepartment: SelectDepartment?,
    navController: NavController,
    viewModel: MyViewModel,
) {
    val state = viewModel.stateSelectService.value

    LaunchedEffect(true) {

        Log.d(
            "TAG",
            "SelectServiceScreen: department id ${selectDepartment?.DepartmentPKID.toString()}"
        )
            while (true) {
                Log.d("TAG", "SelectDepartmentScreen: called GetSelectDepartments" )
                viewModel.readContinue()
                delay(1000)
            }
    }

    if (state.success.isNotBlank()) {
       // navController.popBackStack(Screen.SelectServiceScreen.route, false)
    }
    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    MaterialTheme.colors.surface,
                )
        ) {
            Modifier.padding(innerPadding)
            Column(
                modifier = Modifier.fillMaxSize()
            ) {

               /* Image(
                    painter = painterResource(id = Constants.BACKGROUND_IMAGE),
                    contentScale = ContentScale.FillBounds,
                    contentDescription = "bg",
                    modifier = Modifier.fillMaxSize()
                )*/


            }
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
            HeaderDesign("Passport Read Page","", navController)
                if (state.success.isNotBlank()) {
                    var text = ""
                    if (state.success.trim { it <= ' ' }.length == 0) {

                        text = "No decodable MRZ Found"
                    } else {
                        text = state.success.replace('\r', '\n')
                        var record = MrzParser.parse(state.success.replace('\r', '\n'));

                        text = state.success.replace('\r', '\n') + "\n" + record.givenNames + " " + record.surname + "\n" + record.dateOfBirth + " " + record.dateOfBirth +  "\n" + record.nationality + " " + record.nationality +  "\n" + record.expirationDate + " " + record.expirationDate +  "\n" + record.sex + " " + record.sex
                        System.out.println("Name: " + record.givenNames + " " + record.surname);
                    }
                    Text(
                        text,
                        color = Color.Black,
                        modifier = Modifier.fillMaxWidth().padding(15.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp,
                    )
                }
            if (state.error.isNotBlank()) {
                Text(
                    state.error,
                    color = MaterialTheme.colors.error,
                    modifier = Modifier.fillMaxWidth().padding(15.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp,
                )
            }

                val listState = rememberLazyListState()
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center ,
                    state = listState
                ) {
                    items(state.services) { service ->
                        CustomButton(onClick = {
                            viewModel.onEvent(
                                MyEvent.GetBookTicket(
                                serviceID = service.ServicesPKID.toString(),
                                isHandicap = false,
                                isVip = false,
                                languageID = "0",
                                appointmentCode = "-1",
                                isaapt = false,
                                refid = "-1",
                                DoctorServiceID = "-1",
                                ticketDesignId =service.ServicesTicketDesignerFKID.toString()
                            )
                            )
                        }, text = service.ServicesNameEN
                            ?: "" , textAr =
                        service.ServicesNameAR ?: "",
                        )
                    }
                }




            }

            /* if (state.error.isNotBlank()) {

             }
             if (state.isLoading) {
                 CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
             }
             if (state.success.isNotBlank()) {
                 LaunchedEffect(key1 = true) {

                 }
             }*/
        }
        if (state.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun ServiceInfo(service: SelectService, navController: NavController, onClick: () -> Unit) {

    val fontName = "GE_SS_Two_Bold"

    var fontEnglish = FontFamily(Font(R.font.questrial_regular))
    var fontArabic = FontFamily(Font(R.font.ge_dinar_one_medium))
    val provider = GoogleFont.Provider(
        providerAuthority = "com.google.android.gms.fonts",
        providerPackage = "com.google.android.gms",
        certificates = R.array.com_google_android_gms_fonts_certs
    )
    val fontFamily = FontFamily(
        androidx.compose.ui.text.googlefonts.Font(googleFont = GoogleFont(fontName), provider)
    )
    val arabicBold = TextStyle(fontFamily = fontFamily)

    var DoctorDisplayNameEnFontSIze: TextUnit = with(LocalDensity.current) {
        service.ServicesFontSize?.toInt()?.toSp() ?: 88.toSp()
    }

    //  var DoctorDisplayNameEnFontColor: Color = state.doctorDetail.DoctorDisplayNameEnFontColor?.color ?: "#2e3192".color


    /*  val DoctorDisplayNameEnFontName =state.doctorDetail.DoctorDisplayNameEnFontName ?: fontName
      lateinit var fontFamilyDoctorDisplayNameEnFontName : FontFamily
      if(DoctorDisplayNameEnFontName != "GE_SS_Two_Bold"){
          fontFamilyDoctorDisplayNameEnFontName =  FontFamily(
              androidx.compose.ui.text.googlefonts.Font(googleFont = GoogleFont(DoctorDisplayNameEnFontName),provider))
      }else{
          fontFamilyDoctorDisplayNameEnFontName=  FontFamily(Font(R.font.ge_bold))
      }*/
    Box(
        modifier = Modifier
            .width(250.dp)
            .height(250.dp)
            .padding(horizontal = 8.dp, vertical = 12.dp)
            .background(color = Color.White, shape = RoundedCornerShape(20.dp))
            .clickable {
                onClick()
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .border(BorderStroke(1.dp, primarySidra),
                    shape = RoundedCornerShape(8.dp),),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                var bitmap: ImageBitmap? = null
                if (service.ServicesLogo != null) {
                    try {
                        bitmap = service.ServicesLogo!!.toBitmap().asImageBitmap()
                    } catch (e: java.lang.Exception) {
                        bitmap = null
                    }
                }
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = "",
                        modifier = Modifier
                            .width(250.dp)
                            .height(120.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .width(180.dp)
                            .height(100.dp)
                    )
                }


            }
            Spacer(modifier = Modifier.height(15.dp))

            Text(
                service.ServicesNameEN
                    ?: "",
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                style = TextStyle(fontFamily =fontEnglish),
            )

            Text(
                service.ServicesNameAR ?: "",
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                style = TextStyle(fontFamily =fontEnglish),
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                (service.ServicesDescription
                    ?: "") + "\n" + (service.ServicesDescriptionAr ?: ""),
                color = MaterialTheme.colors.secondary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

        }


    }
}

fun String.toBitmap(): Bitmap {
    Base64.decode(this, Base64.DEFAULT).apply {
        return BitmapFactory.decodeByteArray(this, 0, size)
    }
}

@Composable
fun CustomButton(onClick: () -> Unit, text: String,textAr: String) {
    var fontEnglish = FontFamily(Font(R.font.questrial_regular))
    var fontArabic = FontFamily(Font(R.font.ge_dinar_one_medium))
    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(contentColor = primarySidra),
        border = BorderStroke(1.dp, primarySidra),
        modifier = Modifier
            .width(330.dp)
            .height(170.dp)
            .padding(horizontal = 8.dp, vertical = 12.dp)
            .shadow(15.dp, shape = RoundedCornerShape(5.dp)), shape = RoundedCornerShape(15.dp)

    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text,
                    fontSize = 23.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(fontFamily =fontEnglish),
                )
                Text(
                    textAr,
                    fontSize = 23.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    style = TextStyle(fontFamily =fontArabic),
                )

            }

        }

    }
}

@Composable
fun HeaderDesign(title: String,titleAr: String, navController: NavController) {

    var fontEnglish = FontFamily(Font(R.font.questrial_regular))
    var fontArabic = FontFamily(Font(R.font.ge_dinar_one_medium))
    Box(modifier = Modifier.padding(horizontal = 0.dp, vertical = 0.dp)) {


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White, shape = RoundedCornerShape(10.dp))
                .border(
                    BorderStroke(2.dp, primarySidra)
                )
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.Center,modifier  =Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(title, fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Color.Black,
                            style = TextStyle(fontFamily =fontEnglish),)
                        Text(titleAr, fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Color.Black,
                            style = TextStyle(fontFamily =fontArabic),)

                    }

                }

            }


        }
        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 30.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
        /*    Image(
                painter = painterResource(id = Constants.LOGO),
                contentScale = ContentScale.FillBounds,
                contentDescription = "bg",
                modifier = Modifier
                    .width(180.dp) //sidra
                   // .width(90.dp) //hadi
                    .height(70.dp)
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            if (change.position.y > 400) {
                                navController.navigate(Screen.SettingScreen.route)
                            }
                            change.consume()
                        }
                    }
            )*/
        }
    }

}




