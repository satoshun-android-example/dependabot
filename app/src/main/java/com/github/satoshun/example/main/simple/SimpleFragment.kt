package com.github.satoshun.example.main.simple

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.dropbox.android.external.store4.MemoryPolicy
import com.dropbox.android.external.store4.StoreBuilder
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.get
import com.github.satoshun.example.R
import com.github.satoshun.example.databinding.SimpleFragBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@FlowPreview
class SimpleFragment : Fragment(R.layout.simple_frag) {
  private lateinit var binding: SimpleFragBinding

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding = SimpleFragBinding.bind(view)

    val store = StoreBuilder
      .fromNonFlow<String, String> {
        println("called $it")
        delay(500)
        "test"
      }
      .cachePolicy(
        MemoryPolicy
          .builder()
          .setMemorySize(1024)
          .setExpireAfterWrite(10)
          .build()
      )

    lifecycleScope.launch {
      store.build().stream(StoreRequest.cached("hoge", false)).collect {
        println(it)
      }
    }

    lifecycleScope.launch {
      delay(1000)
      store.build().stream(StoreRequest.cached("hoge", false)).collect {
        println(it)
      }
    }

    lifecycleScope.launch {
      delay(1000)
      val result = store.build().get("hoge")
      println(result)
    }
  }
}
