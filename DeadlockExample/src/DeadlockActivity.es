
@intent1.setComponent("com.vdm.DeadlockActivity")

startActivity(@intent1)

REPEAT 3 {
$button1.onClick()
}



