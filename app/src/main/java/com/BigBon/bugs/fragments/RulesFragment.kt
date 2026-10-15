package com.BigBon.bugs.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.BigBon.bugs.R

class RulesFragment : Fragment() {

    private lateinit var webViewRules: WebView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_rules, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        webViewRules = view.findViewById(R.id.webViewRules)

        val htmlContent = """
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        padding: 16px;
                        background-color: #f5f5f5;
                        color: #333;
                    }
                    h1 {
                        color: #2196F3;
                        font-size: 24px;
                        margin-bottom: 16px;
                    }
                    h2 {
                        color: #1976D2;
                        font-size: 18px;
                        margin-top: 16px;
                        margin-bottom: 8px;
                    }
                    p {
                        font-size: 14px;
                        line-height: 1.6;
                        margin-bottom: 12px;
                    }
                    ul {
                        margin-left: 20px;
                        margin-bottom: 12px;
                    }
                    li {
                        margin-bottom: 8px;
                        font-size: 14px;
                    }
                </style>
            </head>
            <body>
                <h1>Правила игры</h1>

                <h2>Цель игры</h2>
                <p>Ловить жуков на экране и набирать очки за отведенное время.</p>

                <h2>Основные правила</h2>
                <ul>
                    <li>Жуки появляются в случайных местах на экране</li>
                    <li>Нажимайте на жуков, чтобы их поймать</li>
                    <li>За каждого пойманного жука начисляются очки</li>
                    <li>Игра имеет ограниченную продолжительность</li>
                </ul>

                <h2>Настройки</h2>
                <p>В разделе "Настройки" можно изменить параметры игры.</p>

                <p><b>Удачи!</b></p>
            </body>
            </html>
        """.trimIndent()

        webViewRules.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
    }
}
