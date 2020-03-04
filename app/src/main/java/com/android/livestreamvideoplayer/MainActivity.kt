package com.android.livestreamvideoplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.android.livestreamvideoplayer.databinding.ActivityMainBinding
import com.android.multistreamplayer.MultiStreamPlayerLayout

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        (binding.root as MultiStreamPlayerLayout).play("http://video-weaver.fra05.hls.ttvnw.net/v1/playlist/CuEDa5rmX61hsKivWWVtc7x9xX6o_nCCmfBAkt9RylRA4L4Mk2ym_hWtzhLtEYKiNUv3qrJXKXHIlkcpMKhKDjTcd5x38WdQsUAcYYG3fPj_9lo93gJl013KFQN0YG0QN1Pa4kH-pKAaMumwj5_Ukf1v371adFNnv5ZisUx3GdWqoPqsrvpyw4D2eXMOz871gMW4bkXH0n1OahObNIkPCRbFO1sY0q4j61TOLkyqW_73EZ66uPHWagXf0aJzikd_6wqKCtpXvla7piOmOI1LIg4hBZgnSX7HCNr4enal0NmT2-MKf4Jd5hvn1TI9ux89Ipb6oOgBs8M9pJNv5UgzpSLN-atzoIaPABiDQANhRAl-0OlMe7sRQ_oTadOMpX5PJ7TuFJNoXLggM_0HzsOgeOX8R828oQO6bRO1uqg4JWhgDl0MzVMoIwm_PQ82Jf04DRg7dCdJDR9C_Z8z24gRYvII9y2m0iib36PZ_NFy6jNLSR5vTCxAu8oW6RbdSmhdqKu9H-h1kPPZ573v_1DAe1NGmJuWaNoRBjwcVdH1QE2QB4TtBzVflM988SVbR9L1rEeFqk8Yd7g5a1s92MSAJ82XBRrVBG4kmb9aYgiWdF9tdHe9OU2KXCkn8jxfLVi6zTUxwRIQa0Ckk-mOjMPztUPt2LUHLhoM4QsXS7SoeCMn483K.m3u8")
    }

}
