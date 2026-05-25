INSERT INTO period (period_id, days)
SELECT v.period_id, v.days
FROM (
    VALUES
        (1, 'MONDAY'),
        (2, 'TUESDAY'),
        (3, 'WEDNESDAY'),
        (4, 'THURSDAY'),
        (5, 'FRIDAY'),
        (6, 'SATURDAY'),
        (7, 'SUNDAY')
) AS v(period_id, days)
WHERE NOT EXISTS (
    SELECT 1
    FROM period p
    WHERE p.days = v.days
);

SELECT setval(
    pg_get_serial_sequence('period', 'period_id'),
    COALESCE((SELECT MAX(period_id) FROM period), 1)
);

INSERT INTO consent_item (code, name, description, required, active)
SELECT v.code, v.name, v.description, v.required, v.active
FROM (
    VALUES
        ('MARKETING', '마케팅 수신 동의', CAST(NULL AS text), false, true),
        ('ADVERTISEMENT', '광고성 정보 수신 동의', CAST(NULL AS text), false, true),
        ('NOTIFICATION', '알림 수신 동의', CAST(NULL AS text), false, true)
) AS v(code, name, description, required, active)
WHERE NOT EXISTS (
    SELECT 1
    FROM consent_item c
    WHERE c.code = v.code
);

INSERT INTO mogak_category (name)
SELECT v.name
FROM (
    VALUES
        ('자격증'),
        ('대외활동'),
        ('운동'),
        ('인사이트'),
        ('공모전'),
        ('직무공부'),
        ('산업분석'),
        ('어학'),
        ('강연,강의'),
        ('프로젝트'),
        ('스터디'),
        ('기타')
) AS v(name)
WHERE NOT EXISTS (
    SELECT 1
    FROM mogak_category c
    WHERE c.name = v.name
);

INSERT INTO address (name)
SELECT v.name
FROM (
    VALUES
        ('서울특별시'),
        ('경기도'),
        ('세종특별자치시'),
        ('대전광역시'),
        ('광주광역시'),
        ('대구광역시'),
        ('부산광역시'),
        ('울산광역시'),
        ('경상남도'),
        ('경상북도'),
        ('전라남도'),
        ('전라북도'),
        ('충청남도'),
        ('충청북도'),
        ('강원도'),
        ('제주도'),
        ('독도/울릉도')
) AS v(name)
WHERE NOT EXISTS (
    SELECT 1
    FROM address a
    WHERE a.name = v.name
);

INSERT INTO job (name)
SELECT v.name
FROM (
    VALUES
        ('기획/전략'),
        ('법무,사무,총무'),
        ('인사/HR'),
        ('회계/세무'),
        ('마케팅/광고/MD'),
        ('개발/데이터'),
        ('디자인'),
        ('물류/무역'),
        ('운전/운송/배송'),
        ('영업'),
        ('고객상담/TM'),
        ('금융/보험'),
        ('식/음료'),
        ('고객서비스/리테일'),
        ('엔지니어링/설계'),
        ('제조/생산'),
        ('교육'),
        ('건축/시설'),
        ('의료/바이오'),
        ('미디어/문화'),
        ('스포츠'),
        ('공공복지'),
        ('자영업'),
        ('군인'),
        ('의료'),
        ('회계사'),
        ('법무사'),
        ('노무사'),
        ('세무사'),
        ('관세사'),
        ('교사'),
        ('디지털노마드'),
        ('영상제작자'),
        ('크리에이터')
) AS v(name)
WHERE NOT EXISTS (
    SELECT 1
    FROM job j
    WHERE j.name = v.name
);
