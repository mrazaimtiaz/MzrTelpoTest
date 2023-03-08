package com.example.mzrtelpotest.presentation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import com.example.mzrtelpotest.BuildConfig
import com.example.mzrtelpotest.R
import com.gicproject.onepagequeuekioskapp.Screen
import com.gicproject.onepagequeuekioskapp.presentation.MyEvent
import com.gicproject.onepagequeuekioskapp.presentation.MyViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun SettingScreen(
    navController: NavController,
    viewModel: MyViewModel,
) {

    val context = LocalContext.current
    val state = viewModel.stateSetting.value
    val isRefreshing by viewModel.isRefreshingSetting.collectAsState()

    val branchName by viewModel.selectedBranchId.collectAsState()
    val branchId by viewModel.selectedBranchName.collectAsState()
    var isBranchExpanded by remember { mutableStateOf(false) }

    val counterName by viewModel.selectedCounterName.collectAsState()
    val counterId by viewModel.selectedCounterId.collectAsState()
    var isCounterExpanded by remember { mutableStateOf(false) }

    val deptId by viewModel.selectedDepartmentId.collectAsState()
    val deptName by viewModel.selectedDepartmentName.collectAsState()
    var isDepartmentExpanded by remember { mutableStateOf(false) }


    var selectedIndexBranch by remember { mutableStateOf(-1) }
    var selectedIndexCounter by remember { mutableStateOf(-1) }
    var selectedIndexDept by remember { mutableStateOf(-1) }

    val versionCode = BuildConfig.VERSION_CODE
    val versionName = BuildConfig.VERSION_NAME



    LaunchedEffect(true) {
        viewModel.onEvent(MyEvent.GetDepartments)
        viewModel.onEvent(MyEvent.GetBranches)

    }

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colors.primary,
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack(Screen.SelectServiceScreen.route, false)
                    }) {
                        Icon(
                            Icons.Filled.KeyboardArrowLeft,
                            "back arrow",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(100.dp, 100.dp)
                        )
                    }

                },
            )
        },
    ) { innerPadding ->
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = {
                Log.d("TAG", "SettingScreen: swipe refresh")
                viewModel.onEvent(MyEvent.GetBranches)
                viewModel.onEvent(MyEvent.GetDepartments)

            },
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        MaterialTheme.colors.surface,
                    )
            ) {
                Modifier.padding(innerPadding)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(top = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator()
                    }
                    ComposeMenu(
                        label = "Select Branch",
                        selectedText =  if (selectedIndexBranch != -1  && state.branches.size > selectedIndexBranch) state.branches[selectedIndexBranch].BranchNameEn.toString() else "Select Branch",
                        content = {
                            state.branches.forEachIndexed { index, item ->

                                DropdownMenuItem(
                                    onClick = {
                                        Log.d("TAG", "SettingScreen: selected item ${item.BranchNameEn}  ${item.PKID}")
                                        selectedIndexBranch = index
                                        isBranchExpanded = false
                                        viewModel.saveBranch(item.BranchNameEn,item.PKID)


                                    }) {
                                    Text(text = item.BranchNameEn.toString() + " " +item.PKID.toString())
                                }
                            }
                        },
                        menuExpandedState = isBranchExpanded,
                        onDismissMenuView = {
                            isBranchExpanded = false
                        },
                        updateExpandedValue = {
                            isBranchExpanded = true
                        },
                    )
             /*       Spacer(modifier = Modifier.height(30.dp))
                    ComposeMenu(
                        label = "Select Counter",
                        selectedText =  if (selectedIndexCounter != -1 && state.counters.size > selectedIndexCounter) state.counters[selectedIndexCounter].CounterName.toString() else "Select Counter",
                        content = {
                            state.counters.forEachIndexed { index, item ->
                                DropdownMenuItem(
                                    onClick = {
                                        selectedIndexCounter = index
                                        isCounterExpanded = false
                                        viewModel.saveCounter(item.CounterName,item.PKID)
                                    }) {
                                    Text(text = item.CounterName.toString() + " " +item.PKID.toString())
                                }
                            }
                        },
                        menuExpandedState = isCounterExpanded,
                        onDismissMenuView = {
                            isCounterExpanded = false
                        },
                        updateExpandedValue = {
                            isCounterExpanded = true
                        },
                    )*/
                    Spacer(modifier = Modifier.height(30.dp))
                    ComposeMenu(
                        label = "Select Dept",
                        selectedText =  if (selectedIndexDept != -1 && state.department.isNotEmpty() && state.department.size > selectedIndexDept) state.department[selectedIndexDept].DepartmentNameEn.toString() else "Select Department",
                        content = {
                            state.department.forEachIndexed { index, item ->
                                DropdownMenuItem(
                                    onClick = {
                                        selectedIndexDept = index
                                        isDepartmentExpanded = false
                                        viewModel.saveDepartment(item.DepartmentNameEn,item.DepartmentNameAr,item.ParentID)
                                    }) {
                                    Text(text = item.DepartmentNameEn.toString() + " " +item.ParentID.toString())
                                }
                            }
                        },
                        menuExpandedState = isDepartmentExpanded,
                        onDismissMenuView = {
                            isDepartmentExpanded = false
                        },
                        updateExpandedValue = {
                            isDepartmentExpanded = true
                        },
                    )
                    Row() {
                        Text(
                            "Selected Branch: $branchName",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 30.dp)
                        )
                        Spacer(modifier = Modifier.width(30.dp))
                        Text(
                            "Selected Branch Id: $branchId",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 30.dp)
                        )
                    }
                    /*Spacer(modifier = Modifier.height(30.dp))
                    Row() {
                        Text(
                            "Selected counter: $counterName",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 30.dp)
                        )
                        Spacer(modifier = Modifier.width(30.dp))
                        Text(
                            "Selected Counter Id: $counterId",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 30.dp)
                        )
                    }*/
                    Spacer(modifier = Modifier.width(30.dp))
                    Text(
                        "Version Name: $versionName   Version Code: $versionCode ",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 30.dp)
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    Row() {
                        Text(
                            "Selected Dept: $deptName",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 30.dp)
                        )
                        Spacer(modifier = Modifier.width(30.dp))
                        Text(
                            "Selected Dept Id: $deptId",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 30.dp)
                        )
                    }
                    IntentToSetting(context)
                    Button(
                        modifier = Modifier.padding(top = 30.dp),
                        onClick = {
                          //  viewModel.funcPrinterConnect()

                        }) {
                        Icon(
                            Icons.Filled.Settings,
                            "setting",
                            tint = Color.White,
                            modifier = Modifier
                                .size(30.dp, 30.dp)
                        )

                        Text(text = "Print Test", Modifier.padding(start = 10.dp))
                    }

                    Button(
                        modifier = Modifier.padding(top = 30.dp),
                        onClick = {
                       //     viewModel.initializeReaderAgain()

                        }) {
                        Icon(
                            Icons.Filled.Settings,
                            "setting",
                            tint = Color.White,
                            modifier = Modifier
                                .size(30.dp, 30.dp)
                        )

                        Text(text = "Initialize Card Reader Indetiv", Modifier.padding(start = 10.dp))
                    }
                }
                if (state.error.isNotBlank()) {
                }
            }
        }
    }
}

@Composable
fun IntentToSetting(context: Context) {
    Button(
        modifier = Modifier.padding(top = 30.dp),
        onClick = {
            startActivity(context, Intent(Settings.ACTION_SETTINGS), null)

        }) {
        Icon(
            Icons.Filled.Settings,
            "setting",
            tint = Color.White,
            modifier = Modifier
                .size(30.dp, 30.dp)
        )

        Text(text = "Open Settings", Modifier.padding(start = 10.dp))
    }
}


@Composable
fun CloseApp() {
    val activity = (LocalContext.current as? Activity)
    Button(
        modifier = Modifier.padding(top = 30.dp),
        onClick = {
            activity?.finish()
        }) {
        Icon(
            Icons.Filled.Close,
            "close",
            tint = Color.White,
            modifier = Modifier
                .size(30.dp, 30.dp)
        )

        Text(text = "Close App", Modifier.padding(start = 10.dp))
    }
}



@Composable
fun ComposeMenu(
    content: @Composable() () -> Unit,
    menuExpandedState: Boolean,
    selectedText: String,
    updateExpandedValue: () -> Unit,
    onDismissMenuView: () -> Unit,
    label: String
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .border(0.5.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.5f))
            .clickable(
                onClick = {
                    updateExpandedValue()
                },
            ),
    ) {
        Text(label)
        Spacer(modifier = Modifier.width(20.dp))
        ConstraintLayout(
            modifier = Modifier
                .size(width = 300.dp, height = 60.dp)
                .padding(16.dp)
        ) {
            val (label, iconView) = createRefs()
            Text(
                text = selectedText,
                color = Color.Black,
                modifier = Modifier
                    .constrainAs(label) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(iconView.start)
                        width = Dimension.fillToConstraints
                    }
            )

            val displayIcon: Painter = painterResource(
                id = R.drawable.ic_drop_down
            )

            Icon(
                painter = displayIcon,
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp, 20.dp)
                    .constrainAs(iconView) {
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    },
                tint = MaterialTheme.colors.onSurface
            )
            DropdownMenu(
                expanded = menuExpandedState,
                onDismissRequest = { onDismissMenuView() },
                modifier = Modifier
                    .background(MaterialTheme.colors.background)
            ) {
                content()
            }
        }
    }
}


@Composable
fun ErrorMsg(errorMsg: String) {
    Text(
        text = errorMsg,
        color = MaterialTheme.colors.error,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    )
}







