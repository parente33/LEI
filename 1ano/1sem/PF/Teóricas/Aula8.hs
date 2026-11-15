module Aula8 where

dialogo :: String -> IO String
dialogo pergunta = do putStr pergunta
                      resp <- getLine
                      return resp

idade :: IO Int
idade = do putStrLn "Qual é a tua idade?"
           resp <- getLine
           return (read resp)

questionario :: [String] -> IO [String]
questionario [] = return []
questionario (q:qs) = do r <- dialogo q
                         lr <- questionario qs
                         return (r:lr)