-- VERDORA GARDEN SHOWCASE DATA | synthetic records for a public portfolio deployment.
-- Product prices are illustrative UAH values; customer contact details are fictitious.
-- PostgreSQL / Flyway; migration runs exactly once on a fresh database.

-- 50 fictional customers (plus the original admin from V9).
-- @verdora.example emails cannot receive real mail. Each bcrypt digest uses a random unknown password.
WITH seed(name, email, phone_number, password_hash, created_at, updated_at) AS (
    VALUES
    ('Олена Коваленко', 'client001@verdora.example', '+380000010000', '$2a$05$C4G59kk1pk9lXV1iWRZQJ.wjv1leBH5nhYQ1nECBGV9pkQAV80auq', '2026-02-01T09:00:00+00:00', '2026-02-08T09:00:00+00:00'),
    ('Андрій Мельник', 'client002@verdora.example', '+380000010001', '$2a$05$vJKtl8fayoNvG57KdIsjQ.wHfuYuKXetX1hc/H.BZVNa/IH.K.iIS', '2026-02-05T12:13:00+00:00', '2026-02-13T12:13:00+00:00'),
    ('Марія Шевченко', 'client003@verdora.example', '+380000010002', '$2a$05$uC1K8A.PHELPGdqY3sz53OZab8VfpyQGZHC732rOE6zPG6tOab11W', '2026-02-09T15:26:00+00:00', '2026-02-18T15:26:00+00:00'),
    ('Дмитро Бондар', 'client004@verdora.example', '+380000010003', '$2a$05$pYm/CQdlFgM/OEgdKXNntOYSa8hu0mGaDYs87Lj6isH49/hDWaL2i', '2026-02-13T18:39:00+00:00', '2026-02-23T18:39:00+00:00'),
    ('Катерина Савчук', 'client005@verdora.example', '+380000010004', '$2a$05$MypuH8H7YvEgx7sSKSQvxuopuKGHOESixUvX9i19peP2.AGGWYPqy', '2026-02-17T10:52:00+00:00', '2026-02-28T10:52:00+00:00'),
    ('Роман Петренко', 'client006@verdora.example', '+380000010005', '$2a$05$Ek7LMzBhFt8hpKctpr8xXO3CzQ.2SwLtpRvbWz3OCwAC0PBLvgra.', '2026-02-21T13:05:00+00:00', '2026-03-05T13:05:00+00:00'),
    ('Софія Гриценко', 'client007@verdora.example', '+380000010006', '$2a$05$bMWPUAprEFpUN2dfWj9PJu57fE0k/Qr7OcYRN3vDArSTMoobFnGgO', '2026-02-25T16:18:00+00:00', '2026-03-10T16:18:00+00:00'),
    ('Михайло Ткаченко', 'client008@verdora.example', '+380000010007', '$2a$05$q3c2vnM4.JbR8eK9LCs08ud.u6uoQNWgHyDoJj7Ec.jMfKTwnePYC', '2026-03-01T19:31:00+00:00', '2026-03-15T19:31:00+00:00'),
    ('Юлія Кравчук', 'client009@verdora.example', '+380000010008', '$2a$05$sD31lQRGvaAW/hdvOBZDnO5FSr70TjBExApP.NiLAmZ4UCOS/eX3e', '2026-03-05T11:44:00+00:00', '2026-03-20T11:44:00+00:00'),
    ('Тарас Коваль', 'client010@verdora.example', '+380000010009', '$2a$05$uOqnM7FnbKW9dmK4p1veuuUfbQuWr6lCFt76qRTMeM3FAtLnwyIxy', '2026-03-09T14:57:00+00:00', '2026-03-25T14:57:00+00:00'),
    ('Ірина Поліщук', 'client011@verdora.example', '+380000010010', '$2a$05$EduiFKUppChexIZig/3VDuoNH6HqOjrFaBKO1Ptyxf8nux1hR/tD.', '2026-03-13T17:10:00+00:00', '2026-03-30T17:10:00+00:00'),
    ('Олексій Левченко', 'client012@verdora.example', '+380000010011', '$2a$05$JPAeekuylqwCKgMddCt6m.E9HYPN5BbE9jb3GGnLYt5NVo7z3zcf.', '2026-03-17T09:23:00+00:00', '2026-04-04T09:23:00+00:00'),
    ('Анастасія Романюк', 'client013@verdora.example', '+380000010012', '$2a$05$7zJFWoaa6wWHHJ7zW2hl0.pQBmNcDltUIWnebhajoIohZXCTgreRy', '2026-03-21T12:36:00+00:00', '2026-04-09T12:36:00+00:00'),
    ('Богдан Мороз', 'client014@verdora.example', '+380000010013', '$2a$05$Ie20pvBh4Wmjjitrc8ymauvBLmzFtIIvD.EEzEQG/i9J4ykARuCnm', '2026-03-25T15:49:00+00:00', '2026-04-14T15:49:00+00:00'),
    ('Наталія Іванчук', 'client015@verdora.example', '+380000010014', '$2a$05$frB2D/l4srr8S4y6.jeYXuBdAryL2IqrFrRK4oignseQD1Gm5i0nG', '2026-03-29T18:02:00+00:00', '2026-04-19T18:02:00+00:00'),
    ('Владислав Козак', 'client016@verdora.example', '+380000010015', '$2a$05$qAgTI76b7yMJf0gXEV/ZJOqi6sO6zZlIZnlqE4VFmeM6ciez4NsFe', '2026-04-02T10:15:00+00:00', '2026-04-24T10:15:00+00:00'),
    ('Вікторія Лисенко', 'client017@verdora.example', '+380000010016', '$2a$05$syEcWowlVP34YkFdvSSz...HOmSBKpm8qDdDhAaIYn.txbUN.CMQO', '2026-04-06T13:28:00+00:00', '2026-04-29T13:28:00+00:00'),
    ('Ігор Данилюк', 'client018@verdora.example', '+380000010017', '$2a$05$dmmmNX80elmbF4H019QhRuAVwdci.7LqxtpS5jTCKWZifcyN6jDJK', '2026-04-10T16:41:00+00:00', '2026-05-04T16:41:00+00:00'),
    ('Вероніка Захарченко', 'client019@verdora.example', '+380000010018', '$2a$05$B0mI6tNDuCdcjza61nqmMOxOmQaWz9J1udR.w4MikNG1kJ/8jbrP2', '2026-04-14T19:54:00+00:00', '2026-04-21T19:54:00+00:00'),
    ('Денис Білоус', 'client020@verdora.example', '+380000010019', '$2a$05$J3eozEwrYRMFZXZ94FJieu1rbevUrB1miZvDXCpCgHiRPU4KBttFO', '2026-04-18T11:07:00+00:00', '2026-04-26T11:07:00+00:00'),
    ('Поліна Костенко', 'client021@verdora.example', '+380000010020', '$2a$05$loEFjfvGT.Lixqi15NGjVO5ZIXcqVQQhFDZmDKRxe8tAPjBluo.RS', '2026-04-22T14:20:00+00:00', '2026-05-01T14:20:00+00:00'),
    ('Олег Сидоренко', 'client022@verdora.example', '+380000010021', '$2a$05$ZQnKliSQD.77q5S2TRtlAuCvM5yie1KQKYl0vOfnzVUripHM8wHGm', '2026-04-26T17:33:00+00:00', '2026-05-06T17:33:00+00:00'),
    ('Аліна Кравченко', 'client023@verdora.example', '+380000010022', '$2a$05$EqBmV4eZMF3FLVbPNN..se.YDlN3df4iHRb/wZSP0T4s/lVs7e.Sm', '2026-04-30T09:46:00+00:00', '2026-05-11T09:46:00+00:00'),
    ('Євген Климчук', 'client024@verdora.example', '+380000010023', '$2a$05$NaHZMDdqgCE18srI1HzhXOXPXfwSlQg0KbbUV4UFWWRKj7P447.9K', '2026-05-04T12:59:00+00:00', '2026-05-16T12:59:00+00:00'),
    ('Оксана Терещенко', 'client025@verdora.example', '+380000010024', '$2a$05$/ZUspJEzJRyDQc6IgLx90.vp/WQ9BDRPxLsBzvCgeEabUTEsMGCXy', '2026-05-08T15:12:00+00:00', '2026-05-21T15:12:00+00:00'),
    ('Павло Гончар', 'client026@verdora.example', '+380000010025', '$2a$05$H18.dUVPxrrHj4GE1N8Cnu.tu7J1jS7FFfNiii2acJVYXeKnHR3nG', '2026-05-12T18:25:00+00:00', '2026-05-26T18:25:00+00:00'),
    ('Христина Василенко', 'client027@verdora.example', '+380000010026', '$2a$05$S3WCaA.W18AL1sYmEmqAnuPAQzXH4gLntOq9YGRLsKOJfrOHOySjy', '2026-05-16T10:38:00+00:00', '2026-05-31T10:38:00+00:00'),
    ('Назар Марченко', 'client028@verdora.example', '+380000010027', '$2a$05$V8dn1DSvGN7QD1IbOjY7Ru4URtnpxc2rRPoQaU6rW0AqSb3kXowW6', '2026-05-20T13:51:00+00:00', '2026-06-05T13:51:00+00:00'),
    ('Дарина Зінченко', 'client029@verdora.example', '+380000010028', '$2a$05$QbOtjm.NKof.hhcdPU5ilea6HCO09Yz8Cg4YTIj4y8OIuHNtlpNnq', '2026-05-24T16:04:00+00:00', '2026-06-10T16:04:00+00:00'),
    ('Юрій Рибак', 'client030@verdora.example', '+380000010029', '$2a$05$u/jQaY5D2RvWCUA7nwinvO8MoVrNB.62ZoJ6IeWputJwOvvF7IOOy', '2026-05-28T19:17:00+00:00', '2026-06-15T19:17:00+00:00'),
    ('Ганна Мартинюк', 'client031@verdora.example', '+380000010030', '$2a$05$5sqhcKqkI3XGdRZB2HFAQOQBu/fqeMcEKsfsStOT.EieHT4t5CQZ6', '2026-06-01T11:30:00+00:00', '2026-06-20T11:30:00+00:00'),
    ('Віталій Степаненко', 'client032@verdora.example', '+380000010031', '$2a$05$iRpxWJd5jGiNKjVGl7D./O/HYf5FuMuunXgc/SFZEgDc8z5a.O0mu', '2026-06-05T14:43:00+00:00', '2026-06-25T14:43:00+00:00'),
    ('Єлизавета Кириленко', 'client033@verdora.example', '+380000010032', '$2a$05$R7orLVdTnZ6pp/TsUIr5CeK.urXty546N1hBRbso/TdVYfgBnNG7y', '2026-06-09T17:56:00+00:00', '2026-06-30T17:56:00+00:00'),
    ('Сергій Ковтун', 'client034@verdora.example', '+380000010033', '$2a$05$mxS1QvOm.tAS8/uD6.D8Mey26e7LmUd.V8U4kM7im9shCn0hWsYR2', '2026-06-13T09:09:00+00:00', '2026-07-05T09:09:00+00:00'),
    ('Тетяна Кучеренко', 'client035@verdora.example', '+380000010034', '$2a$05$YluM.aPuJ1oKStA6IWi2M.cFrLEHBtd4kQVjtVYw1Pye05PM4x25u', '2026-06-17T12:22:00+00:00', '2026-07-10T12:22:00+00:00'),
    ('Артем Семенюк', 'client036@verdora.example', '+380000010035', '$2a$05$XSByJUXKI0sO.IVedpBJoOY8BxcqJSdQj9BinkS/qehdG.wA6cRla', '2026-06-21T15:35:00+00:00', '2026-07-15T15:35:00+00:00'),
    ('Марта Ковальчук', 'client037@verdora.example', '+380000010036', '$2a$05$bcj4uw6ImkNbZ0UQw/ZQC.ehWx8VEYEBaxhtAWkGc.Qt52NzMVTB6', '2026-06-25T18:48:00+00:00', '2026-07-02T18:48:00+00:00'),
    ('Василь Романенко', 'client038@verdora.example', '+380000010037', '$2a$05$mxTXCCSAyQ.H3sXd3cwqSeg0/n6yzBC2ArzTFCHRZD6MXNVB.aYmS', '2026-06-29T10:01:00+00:00', '2026-07-07T10:01:00+00:00'),
    ('Ніна Чорна', 'client039@verdora.example', '+380000010038', '$2a$05$T4IudMGrof7coIWDDeZbe.tFThZbAoKkT.zyckxCmG.1ogc4TYGka', '2026-07-03T13:14:00+00:00', '2026-07-12T13:14:00+00:00'),
    ('Кирило Бабенко', 'client040@verdora.example', '+380000010039', '$2a$05$ZTvkhE88KxwS8ZYcXJP2Ouf/uqkppu4CDHA4i6W8dFQ3PJdRe3KDm', '2026-07-07T16:27:00+00:00', '2026-07-17T16:27:00+00:00'),
    ('Людмила Остапчук', 'client041@verdora.example', '+380000010040', '$2a$05$RONHo2kGhzdQIxXcZYfj3OuQIRapOig455/dlPwUPSs9gsUqG3n6i', '2026-07-11T19:40:00+00:00', '2026-07-22T19:40:00+00:00'),
    ('Ярослав Макаренко', 'client042@verdora.example', '+380000010041', '$2a$05$VT.vUplAlXidoPiXemJPU.Z4s8TVoqWgzGGoj/r5b4DpvGn15dSC2', '2026-07-15T11:53:00+00:00', '2026-07-27T11:53:00+00:00'),
    ('Анна Бойко', 'client043@verdora.example', '+380000010042', '$2a$05$LT8pRls2nWwfEC7zbd.4TeJGQLBWd9r..8lhxGNCrudd/XGk3D.Oy', '2026-07-19T14:06:00+00:00', '2026-08-01T14:06:00+00:00'),
    ('Петро Соломко', 'client044@verdora.example', '+380000010043', '$2a$05$FQ1seK8LSvghGve3wXaxzOAjvZm0yaK.loQCQ8drfG1Us.GUe9Z5O', '2026-07-23T17:19:00+00:00', '2026-08-06T17:19:00+00:00'),
    ('Світлана Литвин', 'client045@verdora.example', '+380000010044', '$2a$05$PshfpwjHPucbWnmvFf7y9enJp5l/PFvCqFrK1e7rkx2K6ZTXBXfyy', '2026-07-27T09:32:00+00:00', '2026-08-11T09:32:00+00:00'),
    ('Максим Гаврилюк', 'client046@verdora.example', '+380000010045', '$2a$05$h4kYlWPphun12lxt86NW2OUxC.tXj6XKzx3NaVvTq4y7pjEF62wj6', '2026-07-31T12:45:00+00:00', '2026-08-16T12:45:00+00:00'),
    ('Дарія Тимошенко', 'client047@verdora.example', '+380000010046', '$2a$05$HR6Y2axWK9C8UH9hBiD7qevLGmOJtGfyfGLBAIlFNHY42mtcxzEQa', '2026-08-04T15:58:00+00:00', '2026-08-21T15:58:00+00:00'),
    ('Руслан Федоренко', 'client048@verdora.example', '+380000010047', '$2a$05$/B2WFjb4dRFVe1y5DQBHE.V/TXB5H5aoa9eW08a.2.MxaWsyL7qeW', '2026-08-08T18:11:00+00:00', '2026-08-26T18:11:00+00:00'),
    ('Ілля Гнатюк', 'client049@verdora.example', '+380000010048', '$2a$05$F.SebHirSFVZUxldYEViteG9IEr2ljJdCPhvSydE3FBDzXjJT.bKu', '2026-08-12T10:24:00+00:00', '2026-08-31T10:24:00+00:00'),
    ('Ангеліна Паламарчук', 'client050@verdora.example', '+380000010049', '$2a$05$oPtWd96F4DXce.KsmpzX/uG.cry7RRUOjw..Ts4yGHBeedBERodLe', '2026-08-16T13:37:00+00:00', '2026-09-05T13:37:00+00:00')
)
INSERT INTO users(name, email, phone_number, password_hash, role, created_at, updated_at)
SELECT s.name, s.email, s.phone_number, s.password_hash, 'USER',
       s.created_at::timestamptz, s.updated_at::timestamptz
FROM seed s
WHERE NOT EXISTS (SELECT 1 FROM users u WHERE u.email = s.email);
